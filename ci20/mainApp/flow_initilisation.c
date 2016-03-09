#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <flow/flowmessaging.h>

#include "global.h"
#include "utils.h"


/** Define temp data stores
    *
    */
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

/** Set server data config file
    *
    * @param *regData Pointer to the serverData struct
    */
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

/** Set device data config file
    *
    * @param *regData Pointer to the deviceData struct
    */
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

/** Get server information from user input
    *
    * @param *regData Pointer to the serverData struct
    */
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

/** Get device information from user input
    *
    * @param *regData Pointer to the deviceData struct
    */
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

/** Get server information from config file
    *
    * @param *regData Pointer to the serverData struct
    */
static void GetServerConfigDataFile(ServerData *regData)
{
	FILE *configFile = fopen(SERVER_DATA, "r");

	// does it even exist?
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

/** Get device information from config file
    *
    * @param *regData Pointer to the deviceData struct
    */
static void GetDeviceConfigDataFile(DeviceData *regData)
{
	FILE *configFile = fopen(DEVICE_DATA, "r");

	// does it even exist?
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

/** Initialise the FlowMessageing library
    *
    * @param *url The HTTP REST server url
    * @param *key The server oAuth key
    * @param 8secret The server oAuth secret
	* @return bool success
    */
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

/** Login (or register) a device with flow
    *
    * @param *deviceType The device type, e.g. Ci20, Ci40, Wifire 
    * @param *macAddr Devices Mac Address
    * @param *serialNum A serial number for the device
    * @param *version Software version
    * @param *name A name to identify the device
    * @param *regKey Unique registration key generated by the user account (if device is to be linked to a single user)
	* @return bool success
    */
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

/** Get the ID of the logged in device (aka this one)
    *
    * @param *deviceID Buffed to fill with the device ID 
	* @return bool success
    */
bool GetDeviceID(char *deviceID) {
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

/** Initialise the flow connection
    *
    * @param *gData Struct holding the global data
	* @return bool success
    */
bool InitiliseFlow(GlobalData *gData) {
	
	bool result = false;
	
	// set the temp data stores
	ServerData serverData;
	DeviceData deviceData;
		
	// get the server info
	GetServerConfigDataFile(&serverData);
	
	// connect to the server
	if (InitialiseLibFlowMessaging((const char *)serverData.url, (const char*) serverData.key, (const char*) serverData.secret)) {
		// get device info
		GetDeviceConfigDataFile(&deviceData);
		
		// login the device
		if (RegisterDevice(deviceData.deviceType, deviceData.deviceMACAddress, deviceData.deviceSerialNumber, DEVICE_SOFTWARE_VERSION, deviceData.deviceName, deviceData.deviceRegKey)) {
			if (GetDeviceID(gData->FlowID)) {
				printf("Flow init success\n\r");
			
				result = true;
			}
		}
		
	}
	
	return result;
	
}