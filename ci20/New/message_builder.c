#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "message_format.h"

bool TextMessage_Build(char *messageID, char *sender, char *body, char **output) {

	unsigned int xmlSize = 0;
	
	char xml[] = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					"<message>"
						"<messageID>%s</messageID>"
						"<senderID>%s</senderID>"
						"<type>%s</type>"
						"<body>%s</body>"
					"</message>";
					
	xmlSize = strlen(xml) +
				strlen(messageID) +
				strlen(sender) +
				strlen(TEXT_MESSAGE) +
				strlen(body);
					
	*output = (char *)Flow_MemAlloc(xmlSize);
	
	
	if (*output) {
		snprintf(*output, xmlSize, xml,
					messageID,
					sender,
					TEXT_MESSAGE,
					body);
		return true;
	}
	else {
		return false;
	}

}