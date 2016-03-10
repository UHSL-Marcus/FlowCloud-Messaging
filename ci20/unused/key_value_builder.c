#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "key_value_builder.h"

bool KeyValueSetting_Build(ElementNameValuePair data[], int elementCount, char* rootName, char **output) {

	unsigned elementCoreSize = strlen("<></>");
	unsigned xmlSize = 0;
	
	char *stringElements[elementCount];
	
	char rootHead[strlen(rootName)+ strlen("<>")];
	sprintf(rootHead, "<%s>", rootName);
	xmlSize += strlen(rootHead);
	
	char rootFoot[strlen(rootName)+ strlen("</>")];
	sprintf(rootFoot, "</%s>", rootName);
	xmlSize += strlen(rootFoot);
	
	int i;
	for (i = 0; i < elementCount; i++) {
		int size = elementCoreSize + (strlen(data->ElementName)*2) + strlen(data->ElementValue);
		char element[size];
		sprintf(element, "<%s>%s</%s>", data->ElementName, data->ElementValue, data->ElementName);
		xmlSize += strlen(element);
		stringElements[i] = (char*)malloc(sizeof(element));
		strcpy(stringElements[i], element);
	}
	
	printf("Size: %d\n\r", xmlSize);
	*output = (char*)malloc(xmlSize);
	
	if (*output) {
	
		strcat(*output, rootHead);
		
		for (i = 0; i < elementCount; i++) {
			strcat(*output, stringElements[i]);
			free(stringElements[i]);
		}
		
		strcat(*output, rootFoot);
		
		return true;
	} else return false;
	
}

