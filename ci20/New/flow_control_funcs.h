#ifndef FLOW_CONTROL_FUNCS_H
#define FLOW_CONTROL_FUNCS_H

typedef enum
{
	SendMessage_ToUser,
	SendMessage_ToDevice,
}SendMessage_Type;

void RegisterCallbackForReceivedMsg(FlowMessaging_MessageReceivedCallBack callback);
bool SendMessage(char *id, char *message, SendMessage_Type type);

#endif /* FLOW_CONTROL_FUNCS_H */