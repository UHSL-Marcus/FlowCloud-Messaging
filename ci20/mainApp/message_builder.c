#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "message_format.h"

/** Build a text messaage in the correct format
	*
    * Allocates memory for *output, caller must free
	*
    * @param *messageID ID for this message 
	* @param *sender ID of the sender 
	* @param *body The information to be sent
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */
bool TextMessage_BuildMessage(char *messageID, char *sender, char *body, char **output) {

	unsigned int xmlSize = 0;
	
	char xml[] =	"<message>"
						"<messageID>%s</messageID>"
						"<senderID>%s</senderID>"
						"<senderType>%s</senderType>"
						"<type>%s</type>"
						"<body>%s</body>"
					"</message>";
					
	xmlSize = strlen(xml) +
				strlen(messageID) +
				strlen(sender) +
				strlen(SENDER_TYPE_DEVICE) +
				strlen(TEXT_MESSAGE) +
				strlen(body);
					
	*output = (char *)Flow_MemAlloc(xmlSize);
	
	if (*output) {
		snprintf(*output, xmlSize, xml,
					messageID,
					sender,
					SENDER_TYPE_DEVICE,
					TEXT_MESSAGE,
					body);
		return true;
	}
	else {
		Flow_MemFree((void**)&*output);
		return false;
	}

}

/** Builds a generic event message
	*
    * Allocates memory for *output, caller must free
	*
    * @param *type The event type
	* @param *data Custom data for the event 
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */

bool Event_BuildMessage(char *type, char *data, char**output) {
	unsigned int xmlSize = 0;
	
	char xml[] =	"<event>"
						"<type>%s</type>"
						"<body>%s</body>"
					"</event>";
					
	xmlSize = strlen(xml) +
				strlen(type) +
				strlen(data);
					
	*output = (char *)Flow_MemAlloc(xmlSize);
	
	if (*output) {
		snprintf(*output, xmlSize, xml,
					type,
					data);
		return true;
	}
	else {
		Flow_MemFree((void**)&*output);
		return false;
	}
}


/** Builds the heartbeat event message
	*
    * Allocates memory for *output, caller must free
	*
    * @param *timestamp heartbeat timestamp (UTC)
	* @param *uptime heartbeat 
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */
bool HeartbeatEvent_BuildMessage(char *timestamp, char *uptime, char **output) {
	
	bool success = false;

	unsigned int xmlSize = 0;
	
	char xml[] =	"<timestamp>%s</timestamp>"
					"<uptime>%s</uptime>";
					
					
	xmlSize = strlen(xml) +
				strlen(timestamp) +
				strlen(uptime);
				
	char data[xmlSize];
	snprintf(data, xmlSize, xml, timestamp, uptime);
	
	char *event = NULL;
	
	if (Event_BuildMessage(EVENT_HEARTBEAT, data, &event)) {
		
					
		*output = (char *)Flow_MemAlloc(strlen(event));

		if (*output) {
			strcpy(*output, event);
			success = true;
		}
		else {
			Flow_MemFree((void**)&*output);
		}
		
		Flow_MemFree((void**)&event);
	}
	
	return success;
}


/** Builds generic data for input to the key/value store
	*
    * Allocates memory for *output, caller must free
	*
    * @param *type The value type
	* @param *data Custom data
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */
bool KeyValueSetting_BuildData(char *type, char *data, char **output) {
	unsigned int xmlSize = 0;
	
	char xml[] =	"<value>"
						"<type>%s</type>"
						"<body>%s</body>"
					"</value>";
					
	xmlSize = strlen(xml) +
				strlen(type) +
				strlen(data);
					
	*output = (char *)Flow_MemAlloc(xmlSize);
	
	if (*output) {
		snprintf(*output, xmlSize, xml,
					type,
					data);
		return true;
	}
	else {
		Flow_MemFree((void**)&*output);
		return false;
	}
}

/** Builds the heartbeat data for the key/value store
	*
    * Allocates memory for *output, caller must free
	*
    * @param *timestamp heartbeat timestamp (UTC)
	* @param *uptime heartbeat 
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */
bool HeartbeatKeyValueSetting_BuildData(char *timestamp, char *uptime, char **output) {
	
	bool success = false;

	unsigned int xmlSize = 0;
	
	char xml[] =	"<heartbeat>"	
						"<timestamp>%s</timestamp>"
						"<uptime>%s</uptime>"
					"</heartbeat>";
					
					
	xmlSize = strlen(xml) +
				strlen(timestamp) +
				strlen(uptime);
				
	char Heartbeat_Data[xmlSize];
	snprintf(Heartbeat_Data, xmlSize, xml, timestamp, uptime);
	
	char *data = NULL;
	
	if (KeyValueSetting_BuildData(KVS_HEARTBEAT, Heartbeat_Data, &data)) {
		
					
		*output = (char *)Flow_MemAlloc(strlen(data));

		if (*output) {
			strcpy(*output, data);
			success = true;
		}
		else {
			Flow_MemFree((void**)&*output);
		}
		
		Flow_MemFree((void**)&data);
	}
	
	return success;
}
