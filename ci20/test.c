#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <flow/flowcore.h>

int main (int argc, char*argv[])
{
	printf("hello");
	FlowCore_Initialise();
	prinf("goodbye");
	return (0);
}