#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>

#include "flow_control_funcs.h"


void RegisterCallbackForReceivedMsg(FlowMessaging_MessageReceivedCallBack callback) {
	 FlowMessaging_SetMessageReceivedListenerForDevice(callback);
}

bool SendMessage(char *id, char *message, SendMessage_Type type)
{
	return true;
	/*bool success = false;

	FlowMemoryManager memoryManager = FlowMemoryManager_New();

	if (memoryManager)
	{
		switch (type)
		{
			case SendMessage_ToUser:
			{
				if (FlowMessaging_SendMessageToUser((FlowID)id, "text/plain", message, strlen(message), 20))
				{
					printf("Message sent to user = %s\n\r",message);
					//ControllerLog(ControllerLogLevel_Debug, DEBUG_PREFIX "Message sent to user = %s",message);
					success = true;
				}
				else
				{
					printf("Failed to send message to user.\n\r");
					//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to send message to user.");
				}
				break;
			}
			case SendMessage_ToDevice:
			{
				if (FlowMessaging_SendMessageToDevice((FlowID)id, "text/plain", message, strlen(message), 20))
				{
					printf("Message sent to device = %s\n\r", message);
					//ControllerLog(ControllerLogLevel_Debug, DEBUG_PREFIX "Message sent to device = %s",message);
					success = true;
				}
				else
				{
					printf("Failed to send message to device.\n\r");
					//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to send message to device.");
				}
				break;
			}
			default:
			{
				printf("Unknown type to send message");
				//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Unknown type to send message");
				break;
			}
		}
		FlowMemoryManager_Free(&memoryManager);
	}
	else
	{
		printf("Failed to create memory manager.");
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to create memory manager.");
	}
	return success; */
}



