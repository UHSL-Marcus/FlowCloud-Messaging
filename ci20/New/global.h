#ifndef GLOBAL_H
#define GLOBAL_H



#include <flow/flowcore.h>

#define DEVICE_SOFTWARE_VERSION "0.1"

#define QUEUE_WAITING_TIME (10000)
#define MAX_SIZE (50)
#define QUEUE_SIZE (20)
#define DEFAULT_TASK_PRIORITY (1)
#define MIN_STACK_SIZE (4096)

#define SERVER_DATA "flow_controller_server.cnf"
#define DEVICE_DATA "flow_controller_device.cnf"

typedef struct
{
	char FlowID[MAX_SIZE];
	
	FlowThread ContollerThread;
	FlowThread FlowControlThread;

	FlowQueue FlowCommandsQueue;				
	FlowQueue ControlEventsQueue;			
}GlobalData;

#endif