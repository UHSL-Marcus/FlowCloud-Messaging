#ifndef FLOW_CONNECTION_H
#define FLOW_CONNECTION_H


bool InitialiseLibFlowMessaging(const char *url, const char *key, const char *secret);
bool RegisterDevice(char *deviceType, char *macAddr, char *serialNum, char *version, char *name, char *devRegKey);
bool LoginUser(char *username, char *password);



#endif /* FLOW_CONNECTION_H */