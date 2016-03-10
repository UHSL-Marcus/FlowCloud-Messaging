#ifndef KEY_VALUE_BUILDER_H
#define KEY_VALUE_BUILDER_H

#define KEY_VALUE_HEARTBEAT "heartbeat_log"

typedef struct {
	char *ElementName;
	ElementType type;
	void *ElementValue;
}ElementNameValuePair;

enum {
	ElementType_String,
	ElementType_Element,
}ElementType;

bool KeyValueSetting_Build(ElementNameValuePair data[], int elementCount, char* rootName, char **output);

#endif /* KEY_VALUE_BUILDER_H */