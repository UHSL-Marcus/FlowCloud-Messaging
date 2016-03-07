#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>

#include "flow_control_funcs.h"


void RegisterCallbackForReceivedMsg(FlowMessaging_MessageReceivedCallBack callback) {
	 FlowMessaging_SetMessageReceivedListenerForDevice(callback);
}

bool SendMessage(char *id, char *message, SendMessage_Type type)
{
	bool success = false;
	
	printf("Message: %s\nRecipient: %s\n\r", message, id);
	
	FlowMemoryManager memoryManager = FlowMemoryManager_New();
	
	if (memoryManager) {
		switch (type) {
			case SendMessage_ToUser: {
				FlowUser user = FlowUsers_RetrieveUser(memoryManager, id);
				
				printf("User ID: %s\n\r", FlowUser_GetUserID(user));
				printf("User Name: %s\n\r", FlowUser_GetDisplayName(user));
				printf("User email: %s\n\r", FlowUser_GetEmail(user));
				
				
				if (FlowMessaging_SendMessageToUser(FlowUser_GetUserID(user), "text/plain", message, strlen(message), 120)) {
					success = true;
				} else {
					printf("failed to send message. Code: %d\n\r", FlowThread_GetLastError());
				}
	
				break;
			}
		}
		
		FlowMemoryManager_Free(&memoryManager);
	} else {
		printf("failed to create memory manager.\n\r");
	}

	
	return success;
	
}



