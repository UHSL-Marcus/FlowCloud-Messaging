#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <flow/flowcore.h>
#include <flow/flowmessaging.h>

#define MAX_SIZE 500
#define FLOW_SERVER_URL  "http://ws-uat.flowworld.com"
#define FLOW_SERVER_KEY  "Ph3bY5kkU4P6vmtT"
#define FLOW_SERVER_SECRET  "Sd1SVBfYtGfQvUCR"

char *AoR;

static void setServerDetails()
{
        struct stat filestatus;
 
        if (stat("flow_config.cnf", &filestatus) == -1 && errno == ENOENT)
		{
			FILE *configFile = fopen("flow_config.cnf", "w");
			fprintf(configFile, "Server_Address=%s\n", FLOW_SERVER_URL);
			fprintf(configFile, "Oauth_Key=%s\n", FLOW_SERVER_KEY);
			fprintf(configFile, "Oauth_Secret=%s\n", FLOW_SERVER_SECRET);
			fclose(configFile);
		}
}

bool ConnectToFlow()
{
		setServerDetails();
		
        char url[MAX_SIZE];
        char key[MAX_SIZE];
        char secret[MAX_SIZE];
        bool result = false;
		
		if (FlowCore_Initialise())		// Initalise the library
        {
                FlowCore_RegisterTypes();    // Register all types (including unused ones)
                
                printf("FlowCore initialized successfully\n\r");
				
				if (FlowMessaging_Initialise())		// Initalise messaging library
				{
						printf("FlowMessaging Initialised successfully.\n\r");
				
						/* Read Server Details */
						printf("Reading the Server Details...\n\r");
						FILE *configFile = fopen("flow_config.cnf", "r");
						if (configFile)
						{
								fscanf(configFile, "Server_Address=%s\n", url);
								fscanf(configFile, "Oauth_Key=%s\n", key);
								fscanf(configFile, "Oauth_Secret=%s\n", secret);
								fclose(configFile);
								if (strlen(url) != 0 || strlen(key) != 0 || strlen(secret) != 0)
								{
										/* Connect to Flow server */
										if (FlowClient_ConnectToServer(url, key, secret, false))
										{
												printf("Connected to Flow successfully.\n\r");
												
												FlowMemoryManager memoryManager = FlowMemoryManager_New();
												/* Log in as device */
												if (memoryManager)
												{
														if (FlowClient_LoginAsDevice("ci20", "D03110FF7949", "12345", NULL, "0.1", "ci20-message", "S7HXEZLELE"))
														{
																result = true;
																printf("Logged in as device.\n\r");
																
																FlowDevice device = FlowClient_GetLoggedInDevice(memoryManager);
																if (device)
																{
																	FlowService service = FlowDevice_RetrieveFlowMessagingServiceInfo(device);
																	if (service)
																	{
																		FlowServiceSettings serviceSettings = FlowService_RetrieveServiceSettings(service);
																		if (serviceSettings)
																		{
																			AoR = FlowString_Duplicate(FlowServiceSettings_GetIdentity(serviceSettings));
																		    printf("Got AoR.\n\r");
																		}
																	}
																}
																
														}
														else
														{
																printf("Device Login Failed.\n\r");
																printf("ERROR: code %d\n\r", Flow_GetLastError());
														}
														/*Clearing Memory*/
														FlowMemoryManager_Free(&memoryManager);
												}
												else printf("Flow Could not create a FlowMemoryManager for managing memory\n\r");
									
												return result;
										}
										else
										{
												printf("Connected to Flow failed.\n\r");
												printf("ERROR: code %d\n\r", Flow_GetLastError());
										}
								}
								else printf("Invalid configuration file\r\n");
						}
						else printf("No Config File\r\n");
				}
				else
				{
						printf("FlowMessaging Initialisation Failed \n\r");
						printf("ERROR: code %d\n\r", FlowThread_GetLastError());
				}
        }
        else printf("FlowCore failed to initialize\n\r");
        return result;   
}

void sendMessage() {
	printf("send a message\nThis AOR: %s\n\r", AoR);
	FlowMemoryManager memoryManager = FlowMemoryManager_New();
	
	if (memoryManager) {
		
		FlowDevice device = FlowClient_GetLoggedInDevice(memoryManager);
		
		if (FlowDevice_CanRetrieveOwner(device)) {
			FlowUser user = FlowDevice_RetrieveOwner(device);
			user = FlowUser_RetrieveDetails(user);
			
			if (user) {
				FlowDevices devices = FlowUser_RetrieveAllDevices(user, FLOW_DEFAULT_PAGE_SIZE);
				
				if (devices) {
					int loopIndex;
					for (loopIndex = 0; loopIndex < FlowDevices_GetCount(devices); loopIndex++) {
						FlowDevice device = FlowDevices_GetItem(devices, loopIndex);
						
						if (device) {
							FlowService service = FlowDevice_RetrieveFlowMessagingServiceInfo(device);
							
							if (service) {
								FlowServiceSettings serviceSettings = FlowService_RetrieveServiceSettings(service);
								
								if (serviceSettings) {
									char* thisAoR = FlowString_Duplicate(FlowServiceSettings_GetIdentity(serviceSettings));
									printf("AoR: %s\n\r", thisAoR);
									
									//if (strcmp(thisAoR, AoR) != 0) {
										if (FlowDevice_HasDeviceID(device)) {
											printf("Sending...\n\r");
											char* message = "message";
											int messLen = strlen(message);
											FlowInstantMessage IM = FlowInstantMessage_New(memoryManager);
											FlowInstantMessage_SetContentType (IM, "text/plain");
											FlowInstantMessage_SetContent (IM, message);
											
											FlowInstantMessage_SetDestinationDeviceID (IM, FlowDevice_GetDeviceID(device));
											
											if (FlowService_PublishMessage(service, IM)) {
												printf("IM Message Sent\n\r");
											} else printf("IM message failed.\n\r");
																				
											
											if (FlowMessaging_SendMessageToDevice(thisAoR, "text/plain", message, messLen, 20)) {
												printf("Message Sent\n\r");
											} else printf("message failed.\n\r");
										} else printf("no device ID. (code %d)\n\r", FlowThread_GetLastError());
									//}
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	FlowMemoryManager_Free(&memoryManager);
}

void messageReceivedCallBack(FlowMessagingMessage message)
{
        printf("\n\rReceived New Message\n\r");
        printf("\tMessage: %.*s\n\r",FlowMessagingMessage_GetContentLength(message), FlowMessagingMessage_GetContent(message));
        printf("\tMessage Length: %d\n\r",FlowMessagingMessage_GetContentLength(message));;
}

void WaitForMessage()
{
        FlowMessaging_SetMessageReceivedListenerForDevice(messageReceivedCallBack);
        /* to cancel the loop */
		printf("Listening...\n\r");
        printf("S to send\nQ to quit\n\r");
        fflush(stdout);
        int key = getchar();
        while (1)
        {
			if (key == 'S') {
				sendMessage();
			}
			if (key == 'Q') {
				break;
			}
			key = getchar();
        }
		printf("Stopping...\n\r");
}

void main (int argc, char*argv[])
{
	if (ConnectToFlow()) 
		WaitForMessage();
	
	fflush(stdout);
	FlowClient_Logout();
	FlowMessaging_Shutdown();
	FlowCore_Shutdown();
	
	printf("Stopped\r\n");

}