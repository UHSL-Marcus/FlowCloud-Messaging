#ifndef FLOW_THREAD_H
#define FLOW_THREAD_H

typedef struct
{
	char *sender;
	char *data;
}ReceivedMessage;

typedef enum
{
	FlowControlCmd_SendMessageToUser,
}FlowControlCmd_Type;

typedef struct
{
	FlowControlCmd_Type type;
	char *data;
}FlowControlCmd;

void FlowControlThread(FlowThread thread, void *context);


#endif /* FLOW_THREAD_H */