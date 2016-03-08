#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>

#include "global.h"
#include "utils.h"

typedef struct
{
	char deviceType[MAX_SIZE];
	char deviceMACAddress[MAX_SIZE];
	char deviceSerialNumber[MAX_SIZE];
	char deviceName[MAX_SIZE];
	char deviceRegKey[MAX_SIZE];
}DeviceData;

typedef struct
{
	char url[MAX_SIZE];
	char key[MAX_SIZE];
	char secret[MAX_SIZE];
}ServerData;

static void SetServerConfigurationFile(const ServerData *regData)
{
	FILE *configFile = fopen(SERVER_DATA, "w");

	if (configFile)
	{
		fprintf(configFile, "Server_Address=%s\n", regData->url);
		fprintf(configFile, "Auth_Key=%s\n", regData->key);
		fprintf(configFile, "Secret_Key=%s\n", regData->secret);
		fclose(configFile);
	}
}

static void SetDeviceConfigurationFile(const DeviceData *regData)
{
	FILE *configFile = fopen(DEVICE_DATA, "w");

	if (configFile)
	{
		fprintf(configFile, "Device_Type=%s\n", regData->deviceType);
		fprintf(configFile, "MAC_Addr=%s\n", regData->deviceMACAddress);
		fprintf(configFile, "Serial_Num=%s\n", regData->deviceSerialNumber);
		fprintf(configFile, "Device_Name=%s\n", regData->deviceName);
		fprintf(configFile, "Device_Reg_Key=%s\n", regData->deviceRegKey);
		fclose(configFile);
	}
}

static void GetServerConfigData(ServerData *regData)
{
	
	printf("Please provide your own configuration values:\n");
	/* user input for Server Address */
	printf("Enter Server Address: ");
	scanf("%s", regData->url);
	/* user input for Auth Key */
	printf("Enter Auth Key: ");
	scanf("%s", regData->key);
	/* user input for Secret Key */
	printf("Enter Secret Key: ");
	scanf("%s", regData->secret);
	
	SetServerConfigurationFile(regData);
}

static void GetDeviceConfigData(DeviceData *regData)
{
	
	printf("Please provide your own configuration values:\n");
	/* user input for Device Type */
	printf("Enter Device Type:");
	scanf("%s", regData->deviceType);
	/* user input for Serial Number */
	printf("Enter Device Serial Number:");
	scanf("%s", regData->deviceSerialNumber);
	/* user input for device name */
	printf("Enter Device Name:");
	scanf("%s", regData->deviceName);
	printf("Enter Device Registration Token:");
	scanf("%s", regData->deviceRegKey);
	
	char addr[20];
	getMacAddress(addr);
	sprintf(regData->deviceMACAddress, "%s", addr);

	SetDeviceConfigurationFile(regData);
}

static void GetServerConfigDataFile(ServerData *regData)
{
	FILE *configFile = fopen(SERVER_DATA, "r");

	if (!configFile)
	{
		printf("Creating Server configuration file..\n");

		GetServerConfigData(regData);
	}
	else
	{
		fscanf(configFile, "Server_Address=%s\n", regData->url);
		fscanf(configFile, "Auth_Key=%s\n", regData->key);
		fscanf(configFile, "Secret_Key=%s\n", regData->secret);
		fclose(configFile);
	}
}

static bool LoginUser()
{
	char username[MAX_SIZE];
	char password[MAX_SIZE];
	bool result = false;

	/* user input for username */
	printf("Enter username:");
	scanf("%s", username);

	/* user input for password */
	printf("Enter password:");
	scanf("%s", password);
 
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
			printf("User Login Failed.\n\r");
		}
		/*Clearing Memory*/
		FlowMemoryManager_Free(&memoryManager);
	}
	else
	{
		//TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Flow Could not create a FlowMemoryManager for managing memory.");
		printf("Flow Flow Could not create a FlowMemoryManager for managing memory.\n\r");
	}

	return result;
}

static void GetDeviceConfigDataFile(DeviceData *regData)
{
	FILE *configFile = fopen(DEVICE_DATA, "r");

	if (!configFile)
	{
		printf("Creating Device configuration file..\n");
		
		GetDeviceConfigData(regData);
		
	}
	else
	{
		fscanf(configFile, "Device_Type=%s\n", regData->deviceType);
		fscanf(configFile, "MAC_Addr=%s\n", regData->deviceMACAddress);
		fscanf(configFile, "Serial_Num=%s\n", regData->deviceSerialNumber);
		fscanf(configFile, "Device_Name=%s\n", regData->deviceName);
		fscanf(configFile, "Device_Reg_Key=%s\n", regData->deviceRegKey);
		fclose(configFile);
	}
}


static bool InitialiseLibFlowMessaging(const char *url, const char *key, const char *secret)
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
				printf("Failed to connect to server.\n\r");
			}
		}
		else
		{
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Messaging initialization failed.");
			printf("Flow Messaging initialization failed.\n\r");
		}
	}
	else
	{
		//TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Flow Core initialization failed.");
		printf("Flow Core initialization failed.\n\r");
	}
	return false;
}

static bool RegisterDevice(char *deviceType, char *macAddr, char *serialNum, char *version, char *name, char *regKey)
{
	FlowMemoryManager memoryManager = FlowMemoryManager_New();

	if (memoryManager)
	{
		if (FlowClient_LoginAsDevice(deviceType, macAddr, serialNum, NULL, version, name, regKey))
		{
			FlowMemoryManager_Free(&memoryManager);
			return true;
		}
		else
		{
			//TODO
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to login as device.");
			printf("Failed to login as device.\n\r");
		}
		FlowMemoryManager_Free(&memoryManager);
	}
	else
	{
		// TODO
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to create memory manager.");
		printf("Failed to create memory manager.\n\r");
	}
	return false;
}

bool GetDeviceInfo(char *deviceID) {
	FlowMemoryManager memoryManager = FlowMemoryManager_New();

	if (memoryManager) {
		FlowDevice device = FlowClient_GetLoggedInDevice(memoryManager);
		if (device) {
			FlowID temp;

			temp = FlowDevice_GetDeviceID(device);
			strcpy(deviceID, temp);
			
			FlowMemoryManager_Free(&memoryManager);
			return true;
		}
		else {
			printf("Failed to get logged in device.\n\r");
			//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to get logged in device.");
		}
		FlowMemoryManager_Free(&memoryManager);
	}
	else {
		printf("Failed to create memory manager.\n\r");
		//ControllerLog(ControllerLogLevel_Error, ERROR_PREFIX "Failed to create memory manager.");
	}
	return false;
}

bool GetMessagingService(FlowService *service) {
	
}


bool InitiliseFlow(GlobalData *gData) {
	
	bool result = false;
	
	ServerData serverData;
	DeviceData deviceData;
		
	GetServerConfigDataFile(&serverData);
	
	if (InitialiseLibFlowMessaging((const char *)serverData.url, (const char*) serverData.key, (const char*) serverData.secret)) {
		GetDeviceConfigDataFile(&deviceData);
	
		if (RegisterDevice(deviceData.deviceType, deviceData.deviceMACAddress, deviceData.deviceSerialNumber, DEVICE_SOFTWARE_VERSION, deviceData.deviceName, deviceData.deviceRegKey)) {
			if (GetDeviceInfo(gData->FlowID)) {
				printf("Flow init success\n\r");
			
				result = true;
			}
		}
		
	}
	
	return result;
	
}