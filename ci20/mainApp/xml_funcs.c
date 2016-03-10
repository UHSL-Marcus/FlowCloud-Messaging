#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include <flow/flowcore.h>


/** Extract a XML node as a String
    *
    * @param *nodePath Path to the xml node from the root 
	* @param *returnVal Buffer to hold the output 
	* @param root XML root node
	* @return bool success
    */
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

/** Extract the size of the String in an XML node 
    *
    * @param *nodePath Path to the xml node from the root 
	* @param root XML root node
	* @return size_t String size
    */
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
