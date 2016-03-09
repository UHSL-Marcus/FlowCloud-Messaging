#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#include <flow/flowmessaging.h>

#include "global.h"
#include "controller_events.h"
#include "flow_control_funcs.h"
#include "flow_control.h"
#include "xml_funcs.h"
#include "message_format.h"

FlowQueue *ControllerEventsQueue;

/** Free the memory allocated for this command
    *
    * @param *cmd Pointer to the command struct 
    */
static void FreeCmd(FlowControlCmd *cmd)
{
	if (cmd->data) {
		Flow_MemFree((void **)&cmd->data);
	}

	Flow_MemFree((void **)&cmd);
}

/** Create and post an event to the controller thread's queue.  
    *
    * @param *data String holding the event info
    * @param *sender ID for the flow user or device from whom the event was recieved, can be null or empty 
	* @param datasize Size of *data 
	* @param type The type of event
    * @return bool Success
    */
static bool PostToControllerEventsQueue(char *data, char *sender, unsigned datasize, ControllerEvent_Type type) {
	
	bool success = false;
	ControllerEvent *event = NULL;
	ReceivedMessage *receivedMsg = NULL; // used if the event is a received message
	
	if (sender == NULL) { sender = ""; } // if there is no sender

	//Allocated memory should be freed by the receiver
	event = (ControllerEvent *)Flow_MemAlloc(sizeof(ControllerEvent));
	if (event)
	{
		
		event->type = type;
		
		// for now we only have messages
		receivedMsg = (ReceivedMessage *)Flow_MemAlloc(sizeof(ReceivedMessage));

		if (receivedMsg)
		{
			//Allocate one extra byte for data, as the datasize we got from
			//FlowMessagingMessage_GetContentLength() doesn't include null terminator
			receivedMsg->data = (char *)Flow_MemAlloc(datasize + 1);
			receivedMsg->sender = (char *)Flow_MemAlloc(strlen(sender) + 1);
			
			if (receivedMsg->data && receivedMsg->sender)
			{
				strncpy(receivedMsg->data, data, datasize);
				receivedMsg->data[datasize] = '\0'; 	// FlowMessagingMessage_GetContent() does not include a null terminator;	
				strcpy(receivedMsg->sender, sender);
				event->data = receivedMsg;
				success = FlowQueue_Enqueue(*ControllerEventsQueue, event);
			}
			
			if (!success)
			{
				if (receivedMsg->data)
				{
					Flow_MemFree((void **)&receivedMsg->data);
				}

				if (receivedMsg->sender)
				{
					Flow_MemFree((void **)&receivedMsg->sender);
				}

				Flow_MemFree((void **)&receivedMsg);
			}	
		}
		
		if (!success) {
			Flow_MemFree((void **)&event);
		}
	}

	return success;
}


/** Message Recieved handler, called when flow receives a message
    *
    * @param message The recieved message
    */
void MessageReceivedCallBack(FlowMessagingMessage message) {
	
	char *data = NULL, *sender = NULL;
	unsigned datasize= 0;

	data = FlowMessagingMessage_GetContent(message);
	datasize = FlowMessagingMessage_GetContentLength(message);
	sender = FlowMessagingMessage_GetSenderUserID(message); // doesn't work...

	if (!sender)
	{
		//Message is not sent by user, hence it must be a device
		sender = FlowMessagingMessage_GetSenderDeviceID(message); // nor does this... 
	}
	if (!sender)
	{
		// both have failed, just grab it from the XML in the message as a last resort
		TreeNode xmlRoot = TreeNode_ParseXML((uint8_t*)data, strlen(data), true);
		
		size_t size = GetNodeStringSize(SENDER_ID_PATH, xmlRoot);
		if (size > 0) {
			char tempSender[size];
		
			if (xmlRoot) { 
				if (strcmp(ROOT, TreeNode_GetName(xmlRoot)) == 0) {
					if (GetNodeString(SENDER_ID_PATH, tempSender, xmlRoot)) {
						sender = (char*)malloc(size);
						strcpy(sender, tempSender);
					}
				}
			}
		}
		Tree_Delete(xmlRoot);
	}
	
	//TODO: add check that it is for us. 
	
	if (sender) {
		if (!PostToControllerEventsQueue(data, sender, datasize, ControllerEvent_ReceivedMessage)) {
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Posting message received event to controller thread failed");
			printf("Posting message received event to controller thread failed\n\r");
		}
	}else {
		printf("no sender id detected.\n\r");
	}
	
	free(sender); // no longer needed

	//TODO:
	//ControllerLog(ControllerLogLevel_Debug, DEBUG_PREFIX "Received message = %s",data);
	printf("Received message = %s\n\r",data);
	
}


/** Main loop for this thread. Checks for queued commander from the controller thread and calls the appropiate action
    *
    * @param thread Identity of this thread (required param) 
    * @param *context For extra info to be passed to the thread on creation (required param)
    */
void FlowControlThread(FlowThread thread, void *context) {
	
	printf("Flow Control\n\r");
	
	GlobalData *gData = context;	// localise the context parameter
	
	ControllerEventsQueue = &gData->ControlEventsQueue; //pull the queue pointer from the global context
	
	RegisterCallbackForReceivedMsg(MessageReceivedCallBack);
	
	FlowControlCmd *cmd = NULL;
	
	// loop
	for (;;) {
		// wait for command
		cmd = FlowQueue_DequeueWaitFor(gData->FlowCommandsQueue,QUEUE_WAITING_TIME);
		
		// act
		if (cmd != NULL) {
		
			switch(cmd->type) {
				case FlowControlCmd_SendMessageToUser: {
					OutgoingMessage *out = (OutgoingMessage *) cmd->data;
					
					if (out) {
						if (SendMessage(out->recipient, out->message, SendMessage_ToUser)) 
							printf("message sent\n\r");
					}
					FreeCmd(cmd);
					break;
				}
			}
		}	
	
	}
	
}

