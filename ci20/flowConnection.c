#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>
#include "flowConnection.h"



bool InitialiseLibFlowMessaging(const char *url, const char *key, const char *secret)
{
	if (FlowCore_Initialise())
	{
		FlowCore_RegisterTypes();

		if (FlowMessaging_Initialise())
		{
			if (FlowClient_ConnectToServer(url, key, secret, false))
			{
				return true;
			}
			else
			{
				//TODO
				//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to connect to server.");
			}
		}
		else
		{
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Messaging initialization failed.");
		}
	}
	else
	{
		//TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Core initialization failed.");
	}
	return false;
}

bool RegisterDevice(char *deviceType, char *macAddr, char *serialNum, char *version, char *name, char *devRegKey)
{
	FlowMemoryManager memoryManager = FlowMemoryManager_New();

	if (memoryManager)
	{
		if (FlowClient_LoginAsDevice(deviceType, macAddr, serialNum, NULL, version, name, devRegKey))
		{
			FlowMemoryManager_Free(&memoryManager);
			return true;
		}
		else
		{
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to login as device.");
		}
		FlowMemoryManager_Free(&memoryManager);
	}
	else
	{
		// TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to create memory manager.");
	}
	return false;
}

bool LoginUser(char *username, char *password)
{
	result = false;
	FlowMemoryManager memoryManager = FlowMemoryManager_New();
	if (memoryManager)
	{
		if (FlowClient_LoginAsUser(username, password, false))
		{
			result = true;
		}
		else
		{
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "User Login Failed.");
		}
		/*Clearing Memory*/
		FlowMemoryManager_Free(&memoryManager);
	}
	else
	{
		//TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Flow Could not create a FlowMemoryManager for managing memory");
	}

	return result;
}

