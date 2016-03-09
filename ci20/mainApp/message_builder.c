#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "message_format.h"

/** Build a text messaage in the correct format
    *
    * @param *messageID ID for this message 
	* @param *sender ID of the sender 
	* @param *body The information to be sent
	* @param **output Pointer to a buffer to hold the output
	* @return bool success
    */
bool TextMessage_Build(char *messageID, char *sender, char *body, char **output) {

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