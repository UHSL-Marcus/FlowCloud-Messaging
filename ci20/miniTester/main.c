#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
	char *ElementName;
	char *ElementValue;
}ElementNameValuePair;

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

double getAverage(int arr[], int size) {

   int i;
   double avg;
   double sum;

   for (i = 0; i < size; ++i) {
      sum += arr[i];
   }

   avg = sum / size;

   return avg;
}

void main (int argc, char*argv[]) {
	
	/* an int array with 5 elements */
   int balance[5] = {1000, 2, 3, 17, 50};
   double avg;

   /* pass pointer to the array as an argument */
   avg = getAverage( balance, 5 ) ;
 
   /* output the returned value */
   printf( "Average value is: %f \n\r", avg );
	
	char *data = NULL;
	
	ElementNameValuePair evp1;
	evp1.ElementName = "Name1";
	evp1.ElementValue = "Value1";
	
	ElementNameValuePair evp2;
	evp2.ElementName = "Name2";
	evp2.ElementValue = "Value2";
	
	ElementNameValuePair evpa[2] = {evp1, evp2};

	printf("evpa size: %d\n\r", sizeof(evpa) / sizeof(evpa[0]));
	
	
	if (KeyValueSetting_Build(evpa, sizeof(evpa) / sizeof(evpa[0]), "root", &data)) {
		printf("Data: %s\n\r", data);
	} else printf("failed.\n\r");
	

}
