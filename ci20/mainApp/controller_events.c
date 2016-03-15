#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>

#include <flow/flowmessaging.h>

#include "controller_events.h"
#include "flow_control.h"
#include "global.h"
#include "message_format.h"
#include "message_builder.h"
#include "xml_funcs.h"


FlowQueue *FlowCommandsQueue;
GlobalData *gData;

/** Free the memory allocated for this event
    *
    * @param *event Pointer to the event struct 
    */
static void FreeEvent(ControllerEvent *event)
{
	if (event->data) {
		Flow_MemFree((void **)&event->data);
	}

	Flow_MemFree((void **)&event);
}


/** Create and post a command to the flow control thread's queue.  
    *
	* Allocates memory for FlowControlCmd, receiver must free. 
	*
    * @param *data String holding the command info
    * @param *recipient ID for the flow user or device if the command is to send a message, can be null or empty 
	* @param type The type of flow command being queued
    * @return bool Success
    */
static bool PostToFlowCommandsQueue(void *data, FlowControlCmd_Type type) {
	
	FlowControlCmd *cmd = NULL;
	
	//Allocated memory should be freed by the receiver
	cmd = (FlowControlCmd *)Flow_MemAlloc(sizeof(FlowControlCmd));
	
	if (cmd) {
		cmd->type = type;
		
		cmd->data = data;
			
		if (FlowQueue_Enqueue(*FlowCommandsQueue, cmd)) 
			return true;

		// if we are here then the enqueue failed, release memory	
		Flow_MemFree((void **)&cmd);

	}
	
	return false;
	
}

/** Fires off the heartbeat command to the flow control
	*
	* Allocates memeory for EventToPublish, reciever must free.
	*
	* @param taskID indentify the currently scheduled task (if multiple are handled by this callback function)
	* @param *context any additional data passed to the callback function
    */
void doHeartBeat(FlowTaskID taskID, void *context) {
	printf("doing heartbeat\r\n");
	
	EventToPublish *event = Flow_MemAlloc(sizeof(EventToPublish));
	
	if (event) {
		event->topic = FLOW_MESSAGING_TOPIC_DEVICE_PRESENCE;
		event->contentType = "text/plain";
		event->expirySeconds = 5;
		
		char *content = NULL;
		
		if (HeartbeatEvent_BuildMessage("placeholder", "2 mins", &content)) {
			event->content = (char*)Flow_MemAlloc(strlen(content)); 
			strcpy(event->content, content);
			//event->content = content;
			
			if (!PostToFlowCommandsQueue(event, FlowControlCmd_Publish)) {
				printf("heartbeat post failed.\n\r");
				if (event->content)
					Flow_MemFree((void **)&event->content);
				
				Flow_MemFree((void **)&event);
			}
			
			Flow_MemFree((void **)&content); // memory is allocated in HeartbeatEvent_BuildMessage
			
		}
		
		
	}
}

/** Handles an incomming text message from the flow control 
    *
	* Allocates memeory for OutgoingMessage, reciever must free.
	*
    * @param root Root node of the XML message format
    * @param *sender Sender ID
    */
static void MessageType_TextMessage(TreeNode root, char* sender) {
	
	// set each char buff to the correct size
	char message[GetNodeStringSize(BODY_PATH, root)], 
		messageID[GetNodeStringSize(MESSAGE_ID_PATH, root)],
		senderType[GetNodeStringSize(SENDER_TYPE_PATH, root)];
		
	char *data = NULL;
	
	
	if (GetNodeString(BODY_PATH, message, root) &&
		GetNodeString(MESSAGE_ID_PATH, messageID, root) &&
		GetNodeString(SENDER_TYPE_PATH, senderType, root)) {
			
			// build the reply, simply cat the term "Reply->" to the front of the orignal messsage
			char reply[] = "Reply->";
			strcat(reply, message);
		
			if (TextMessage_BuildMessage(messageID, gData->FlowID, reply, &data)) {
				FlowControlCmd_Type cmd;
				
				if (strcmp(senderType, SENDER_TYPE_USER) == 0)
					cmd = FlowControlCmd_SendMessageToUser;
				if (strcmp(senderType, SENDER_TYPE_DEVICE) == 0)
					cmd = FlowControlCmd_SendMessageToDevice;
				
				OutgoingMessage *out = (OutgoingMessage *)Flow_MemAlloc(sizeof(OutgoingMessage));
					
				if (out) {
					out->recipient = (char*)Flow_MemAlloc(strlen(sender));
					out->message = (char*)Flow_MemAlloc(strlen(data));
					
					strcpy(out->recipient, sender);
					strcpy(out->message, data);
					
					if (!PostToFlowCommandsQueue(out, cmd)) { 
						printf("Posting to flow queue failed.\n\r");
						if (out->recipient)
							Flow_MemFree((void **)&out->recipient);
						if (out->message)
							Flow_MemFree((void**)&out->message);
						
						Flow_MemFree((void**)&out);
					}
						
					
				}
			
				//Flow_MemFree((void **)&data); // memory is allocated in TextMessage_Build	
			}
		
			
		
	}
	
}

/** Parse the message received from flow, pass to the appropite handler
    *
    * @param *data String holding the message info
    */
static void ParseMessage(char* data) {
	
	// a message type event would only be in this format
	ReceivedMessage *receivedMsg = (ReceivedMessage *) data;

	// Flow built-in XML parsing
	TreeNode xmlRoot = TreeNode_ParseXML((uint8_t*)receivedMsg->data, strlen(receivedMsg->data), true);	

	if (xmlRoot) {
		char nodeData[GetNodeStringSize(TYPE_PATH, xmlRoot)];
		
		if (strcmp(ROOT, TreeNode_GetName(xmlRoot)) == 0) {
			if (GetNodeString(TYPE_PATH, nodeData, xmlRoot)) {
				if (strcmp(TEXT_MESSAGE, nodeData) == 0) {
					MessageType_TextMessage(xmlRoot, receivedMsg->sender);
				}
			}
		}
	}
	
	// free the memory
	Tree_Delete(xmlRoot);
	xmlRoot = NULL;
}

/** Main loop for this thread. Checks for queued events from the flow thread and sends them to the correct handler/parser
    *
    * @param thread Identity of this thread (required param) 
    * @param *context For extra info to be passed to the thread on creation (required param)
    */
void ControllerThread(FlowThread thread, void *context) {
	
	printf("Controller Thread\n\r");
	
	// set heartbeat schedule
	FlowScheduler_ScheduleTask(doHeartBeat, NULL, 5, true);
	
	gData = context;	
	
	FlowCommandsQueue = &gData->FlowCommandsQueue; //pull the queue pointer from the global struct
	
	ControllerEvent *event = NULL;
	
	// loop
	for (;;) {
		// wait for an event
		event = FlowQueue_DequeueWaitFor(gData->ControlEventsQueue,QUEUE_WAITING_TIME);
		
		// handle
		if (event != NULL) {
		
			switch(event->type) {
				case ControllerEvent_ReceivedMessage: {
					ParseMessage(event->data);
					FreeEvent(event);
					break;
				}
			}
		}	
	
	}
	
}

