#ifndef CONTROLLER_EVENTS_H
#define CONTROLLER_EVENTS_H

#include <flow/flowcore.h>

typedef enum {
	ControllerEvent_ReceivedMessage,
}ControllerEvent_Type;

typedef struct {
	char *recipient;
	char *message;
}OutgoingMessage;

typedef struct {
	ControllerEvent_Type type;
	void *data;
}ControllerEvent;


void ControllerThread(FlowThread thread, void *context);



#endif /* CONTROLLER_EVENTS_H */