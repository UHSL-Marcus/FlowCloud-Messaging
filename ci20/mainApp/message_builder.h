#ifndef MESSAGE_BUILDER_H
#define MESSAGE_BUILDER_H

bool TextMessage_BuildMessage(char *messageID, char *sender, char *body, char **output);
bool HeartbeatEvent_BuildMessage(char *timestamp, char *uptime, char **output);
bool HeartbeatKeyValueSetting_BuildData(char *timestamp, char *uptime, char **output);

#endif /* MESSAGE_BUILDER_H */