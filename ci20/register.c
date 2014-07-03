#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <flow/flowcore.h>

#define MAX_SIZE 250
#define FLOW_SERVER_URL  "http://ws-uat.flowworld.com"
#define FLOW_SERVER_KEY  "Ph3bY5kkU4P6vmtT"
#define FLOW_SERVER_SECRET  "Sd1SVBfYtGfQvUCR"

bool InitialiseLibFlowCore()
{
        bool result = false;
        if (FlowCore_Initialise())
        {
                FlowCore_RegisterTypes();    // Register all types (including unused ones)
                result = true;
                printf("FlowCore initialized successfully\n\r");
        }
        else
        {
                printf("FlowCore failed to initialize\n\r");
        }
        return result;
}

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
        char url[MAX_SIZE];
        char key[MAX_SIZE];
        char secret[MAX_SIZE];
        bool result = false;
 
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
                                result = true;
                                printf("Connected to Flow successfully.\n\r");
                        }
                        else
                        {
                                printf("Connected to Flow failed.\n\r");
                                printf("ERROR: code %d\n\r", Flow_GetLastError());
                        }
                }
                else
                        printf("Invalid configuration file\r\n");
        }
        return result;
}

bool RegisterDevice()
{
        FlowMemoryManager memoryManager = FlowMemoryManager_New();
        
        bool result = false;
 
        if (memoryManager)
        {
                if (FlowClient_LoginAsDevice("ci20", "d03110ff7949", "", NULL, "0.1", "ci20 MessageBoard", "UO5MYL7WUJ"))
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
        else
        {
                printf("Flow Could not create a FlowMemoryManager for managing memory\n\r");
        }
 
        return result;
}


int main (int argc, char*argv[])
{
	int result = -1;
        if (InitialiseLibFlowCore())
        {
			setServerDetails();
			if (ConnectToFlow())
			{
					if (RegisterDevice())
					{
							result = 0;
							printf("LoggedIn success:Enjoy Flow features\n\r");
					}
			}
			
			//de-initalise flow core
			FlowCore_Shutdown();
        }
        return result;
}
	

