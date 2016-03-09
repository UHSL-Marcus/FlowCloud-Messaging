#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>
#include "flow_control_funcs.h"
#include "global.h"



/** Register the method for flow to use as the revieved message listener
    *
    * @param callback The function to act as message listener
    */
void RegisterCallbackForReceivedMsg(FlowMessaging_MessageReceivedCallBack callback) {
	 FlowMessaging_SetMessageReceivedListenerForDevice(callback);
}

/** Send a flow message to a user or device
    *
    * @param *id User or device ID
    * @param *message The message to send
    * @param type The type of message, To user or To Device
	* @return bool success
    */
bool SendMessage(char *id, char *message, SendMessage_Type type)
{
	bool success = false;
	
	//printf("Message: %s\nRecipient: %s\n\r", message, id);
	
	FlowMemoryManager memoryManager = FlowMemoryManager_New();
	
	if (memoryManager) {
		switch (type) {
			case SendMessage_ToUser: {
				// got to use InstantMessage as FlowMessaging_SendMessageToUser() doesn't work
				FlowInstantMessage IM = FlowInstantMessage_New(memoryManager);
				FlowInstantMessage_SetContentType (IM, "text/plain");
				FlowInstantMessage_SetContent(IM, (char*)message);
			
				FlowUser user = FlowUsers_RetrieveUser (memoryManager, id); // may not be needed, test
			
				FlowInstantMessage_SetDestinationUserID (IM, FlowUser_GetUserID(user));
				
				// do it this way as storing the service in the global struct causes segmentation faults when we try to access it.
				FlowDevice device = FlowClient_GetLoggedInDevice(memoryManager);
				if (device) {
					FlowService service = FlowDevice_RetrieveFlowMessagingServiceInfo(device);
							
					if (service) {
						FlowServiceSettings serviceSettings = FlowService_RetrieveServiceSettings(service);
					
						if (serviceSettings) {
							if (FlowService_PublishMessage(service, IM)) {
								success = true;
							} else {
								printf("failed to send message. Code: %d\n\r", FlowThread_GetLastError());
							}
						}
					}
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






