#ifndef CONTROLLER_EVENTS_H
#define CONTROLLER_EVENTS_H

#include <flow/flowcore.h>

#define HEARTBEAT_TIMESTAMP "timestamp"
#define HEARTBEAT_UPTIME "uptime"

typedef enum {
	ControllerEvent_ReceivedMessage,
}ControllerEvent_Type;

typedef struct {
	char *recipient;
	char *message;
}OutgoingMessage;

typedef struct {
	char *key;
	char *value;
}KeyValueSetting;

typedef struct {
	char *topic;
	char *contentType;
	char *content;
	uint expirySeconds;
}EventToPublish;

typedef struct {
	ControllerEvent_Type type;
	void *data;
}ControllerEvent;


void ControllerThread(FlowThread thread, void *context);
void HeartbeatThread(FlowThread thread, void *context);



#endif /* CONTROLLER_EVENTS_H */