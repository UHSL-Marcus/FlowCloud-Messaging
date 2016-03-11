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

	unsigned int xmlSize = 0;
	
	char xml[] =	"<heartbeat>"
						"<timestamp>%s</timestamp>"
						"<uptime>%s</uptime>"
					"</heartbeat>";
					
	xmlSize = strlen(xml) +
				strlen(timestamp) +
				strlen(uptime);
					
	*output = (char *)Flow_MemAlloc(xmlSize);
	
	
	if (*output) {
		snprintf(*output, xmlSize, xml,
					timestamp,
					uptime);
		return true;
	}
	else {
		return false;
	}

}