#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>

#include <flow/flowmessaging.h>

#include "global.h"
#include "controller_events.h"
#include "flow_control_funcs.h"
#include "flow_control.h"

FlowQueue *ControllerEventsQueue;

static void FreeCmd(FlowControlCmd *cmd)
{
	if (cmd->data) {
		Flow_MemFree((void **)&cmd->data);
	}

	Flow_MemFree((void **)&cmd);
}

static bool PostToControllerEventsQueue(char *data, char *sender, unsigned datasize, ControllerEvent_Type type) {
	
	bool success = false;
	ControllerEvent *event = NULL;
	ReceivedMessage *receivedMsg = NULL;
	
	if (sender == NULL) { sender = ""; }

	//Allocated memory should be freed by the receiver
	event = (ControllerEvent *)Flow_MemAlloc(sizeof(ControllerEvent));
	if (event)
	{
		
		event->type = type;
		
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
				receivedMsg->data[datasize] = '\0'; 		// add null terminator
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


void MessageReceivedCallBack(FlowMessagingMessage message)
{
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

	if (!PostToControllerEventsQueue(data, sender, datasize, ControllerEvent_ReceivedMessage))
	{
		//TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Posting message received event to controller thread failed");
		printf("Posting message received event to controller thread failed\n\r");
	}

	//TODO:
	//ControllerLog(ControllerLogLevel_Debug, DEBUG_PREFIX "Received message = %s",data);
	printf("Received message = %s\n\r",data);
}

void FlowControlThread(FlowThread thread, void *context) {
	
	printf("Flow Control\n\r");
	
	GlobalData *gData = context;	// localise the context parameter
	
	ControllerEventsQueue = &gData->ControlEventsQueue; //pull the queue pointer from the global context
	
	RegisterCallbackForReceivedMsg(MessageReceivedCallBack);
	
	FlowControlCmd *cmd = NULL;
	
	for (;;) {
		cmd = FlowQueue_DequeueWaitFor(gData->FlowCommandsQueue,QUEUE_WAITING_TIME);
		
		if (cmd != NULL) {
		
			switch(cmd->type) {
				case FlowControlCmd_SendMessageToUser: {
					SendMessage(gData->FlowID, cmd->data, SendMessage_ToUser);
					FreeCmd(cmd);
					break;
				}
			}
		}	
	
	}
	
}

