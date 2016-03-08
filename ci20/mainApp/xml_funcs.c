#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include <flow/flowcore.h>

bool GetNodeString(char* nodePath, char* returnVal, TreeNode root) {
	
	TreeNode node = TreeNode_Navigate (root, nodePath);
	
	if (node) {
		char* buff = (char*)TreeNode_GetValue(node);
		
		if (buff && *buff) {
			printf("buff: %s\n\r", buff);
			strcpy(returnVal, buff);
			return true;
		}
		printf("buff failed\n\r");
	}
	return false;
}

size_t GetNodeStringSize(char* nodePath, TreeNode root) {
	TreeNode node = TreeNode_Navigate (root, nodePath);
	printf("node path: %s\n\r", nodePath);
	
	if (node) {
		char* buff = (char*)TreeNode_GetValue(node);
		
		if (buff && *buff) {
			size_t size = strlen(buff)+1;
			printf("size: %d\n\r", size);
			return size;
		}
	}
	
	return 0;
	
}