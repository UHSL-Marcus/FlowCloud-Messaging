#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <errno.h>

#include "controller_events.h"
#include "flow_control.h"
#include "global.h"
#include "flow_initilisation.h"


static GlobalData gData = {};

static bool StartThreads() {
	
	gData.ContollerThread = FlowThread_New("ControllerThread", DEFAULT_TASK_PRIORITY, MIN_STACK_SIZE, ControllerThread, &gData);
	gData.FlowControlThread = FlowThread_New("FlowControlThread", DEFAULT_TASK_PRIORITY, MIN_STACK_SIZE, FlowControlThread, &gData);
	
	if (&gData.ContollerThread == NULL) {
		printf("ControllerThread failed.\n\r");
		return false;
	}
	if (&gData.FlowControlThread == NULL) {
		printf("FlowControlThread failed.\n\r");
		return false;
	}
	
	return true;
} 

int main(int argc, char *argv[])
{

	int result = -1;
	//GlobalData this = &gdata;
	
	//initilise flow
	if (InitiliseFlow(&gData)) {
		
	
		// set the interthread message queues
		gData.FlowCommandsQueue = FlowQueue_NewBlocking(QUEUE_SIZE);
		gData.ControlEventsQueue = FlowQueue_NewBlocking(QUEUE_SIZE);
		
		
		//start the threads
		if (StartThreads()) {
			// wait for exit
			printf("Running...\n\r");
			printf("Type 'Q' to quit.\n\r");
			int key = getchar();
			while (key != 'Q')
			{
					key = getchar();
			}
			printf("Stopping...\n\r");
		}
		
		
		FlowThread_Free(&gData.ContollerThread);
		FlowThread_Free(&gData.FlowControlThread);
		
		FlowQueue_Free(&gData.FlowCommandsQueue);
		FlowQueue_Free(&gData.ControlEventsQueue);
	
	}
	
	printf("Stopped.\n\r");

	return result;
	
}