#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>

#include "controller_events.h"
#include "flow_control.h"
#include "global.h"
#include "message_format.h"
#include "message_builder.h"
#include "xml_funcs.h"

FlowQueue *FlowCommandsQueue;

GlobalData *gData;

static void FreeEvent(ControllerEvent *event)
{
	if (event->data) {
		Flow_MemFree((void **)&event->data);
	}

	Flow_MemFree((void **)&event);
}

static bool PostToFlowCommandsQueue(char *data, char* recipient, FlowControlCmd_Type type) {
	
	printf("MESSAGE: %s\nRECIPIENT: %s\n\r", data, recipient);
	
	FlowControlCmd *cmd = NULL;
	
	//Allocated memory should be freed by the receiver
	cmd = (FlowControlCmd *)Flow_MemAlloc(sizeof(FlowControlCmd));
	
	if (cmd) {
		cmd->type = type;
		
		switch (type) {
			case FlowControlCmd_SendMessageToUser: {
				OutgoingMessage *out = (OutgoingMessage *)Flow_MemAlloc(sizeof(OutgoingMessage));
					
				if (out) {
					out->recipient = (char*)Flow_MemAlloc(strlen(recipient));
					out->message = (char*)Flow_MemAlloc(strlen(data));
					
					strcpy(out->recipient, recipient);
					strcpy(out->message, data);
					cmd->data = out;
					
					if (FlowQueue_Enqueue(*FlowCommandsQueue, cmd)) 
						return true;
					else {
						if (out->recipient)
							Flow_MemFree((void **)&out->recipient);
						if (out->message)
							Flow_MemFree((void**)&out->message);
						
						Flow_MemFree((void**)&out);
					}
				}
				break;
			}
		}
		
		
		
		// if we are here then the enqueue failed, release memory	
		Flow_MemFree((void **)&cmd);
		
		
	}
	
	return false;
	
}

static void MessageType_TextMessage(TreeNode root, char* sender) {
	char message[GetNodeStringSize(BODY_PATH, root)], 
		messageID[GetNodeStringSize(MESSAGE_ID_PATH, root)],
		senderType[GetNodeStringSize(SENDER_TYPE_PATH, root)];
		
	char *data = NULL;
	
	
	if (GetNodeString(BODY_PATH, message, root) &&
		GetNodeString(MESSAGE_ID_PATH, messageID, root) &&
		GetNodeString(SENDER_TYPE_PATH, senderType, root)) {
			
			char reply[] = "Reply->";
			strcat(reply, message);
		
			if (TextMessage_Build(messageID, gData->FlowID, reply, &data)) {
				FlowControlCmd_Type cmd;
				printf("cmd: %d\n\r", (int)cmd);
				
				if (strcmp(senderType, SENDER_TYPE_USER) == 0)
					cmd = FlowControlCmd_SendMessageToUser;
				if (strcmp(senderType, SENDER_TYPE_DEVICE) == 0)
					cmd = FlowControlCmd_SendMessageToDevice;
				
				printf("cmd: %d\n\r", (int)cmd);
			
				if (!PostToFlowCommandsQueue(data, sender, cmd)) { 
					printf("Posting to flow queue failed.\n\r");
				
				}
			}
		
		Flow_MemFree((void **)&data); // memory is allocated in TextMessage_Build	
		
	}
	
}

static void ParseMessage(char* data) {
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
	printf("deleteing tree root\n\r");
	Tree_Delete(xmlRoot);
	xmlRoot = NULL;
}

void ControllerThread(FlowThread thread, void *context) {
	
	printf("Controller Thread\n\r");
	
	gData = context;	// localise the context parameter
	
	FlowCommandsQueue = &gData->FlowCommandsQueue; //pull the queue pointer from the global context
	
	ControllerEvent *event = NULL;
	
	for (;;) {
		event = FlowQueue_DequeueWaitFor(gData->ControlEventsQueue,QUEUE_WAITING_TIME);
		
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

