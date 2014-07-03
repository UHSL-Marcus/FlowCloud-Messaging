#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <flow/flowcore.h>
#include <flow/flowmessaging.h>

#define MAX_SIZE 250
#define FLOW_SERVER_URL  "http://ws-uat.flowworld.com"
#define FLOW_SERVER_KEY  "Ph3bY5kkU4P6vmtT"
#define FLOW_SERVER_SECRET  "Sd1SVBfYtGfQvUCR"

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
														if (FlowClient_LoginAsDevice("ci20", "00552288661155", "103155AX", NULL, "0.1", "ci20 MessageBoard2", "UDNYMIFDYG"))
														{
																result = true;
																printf("Logged in as device.\n\r");
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

void MessageReplyAsyncCallBack(void *context,bool result)
{
    if(result)
      printf("\n\rAsyncMessage Response success");
    else
      printf("\n\rAsyncMessage Resopnse failed");
}

void messageReceivedCallBack(FlowMessagingMessage message)
{
        char response[MAX_SIZE];
        void *context;
        printf("\n\rReceived New Message\n\r");
        printf("\tMessage: %.*s\n\r",FlowMessagingMessage_GetContentLength(message), FlowMessagingMessage_GetContent(message));
        printf("\tMessage Length: %d\n\r",FlowMessagingMessage_GetContentLength(message));
        stpcpy(response, "reply: ");
		strcat(response, FlowMessagingMessage_GetContent(message));
        FlowMessaging_ReplyToMessageAsync(message, "text/plain", response, strlen(response), 100, MessageReplyAsyncCallBack,context);
        fflush(stdout);
}

void WaitForMessage()
{
        FlowMessaging_SetMessageReceivedListenerForDevice(messageReceivedCallBack);
        /* to cancel the loop */
		printf("Listening...\n\r");
        printf("Press enter key to stop:\n\r");
        fflush(stdout);
        int key = getchar();
        while (key == EOF)
        {
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
	

