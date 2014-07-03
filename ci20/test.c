#include <stdio.h>
#include <stdbool.h>
#include <flow/flowcore.h>

int main (int argc, char*argv[])
{
	printf("hello");
	if(FlowCore_Initialise()) printf("init");
	return (0);
}