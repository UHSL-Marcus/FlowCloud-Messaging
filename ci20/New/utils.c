#include <stdio.h>
#include <string.h>

#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>   
#include <unistd.h>   

#include "utils.h"

void getMacAddress(char *addr) {
	int fd;
    struct ifreq ifr;
    char *iface = "eth0"; // just use eth0 for now. 
	//char addr[22];

    memset(&ifr, 0, sizeof(ifr));

    fd = socket(AF_INET, SOCK_DGRAM, 0);

    ifr.ifr_addr.sa_family = AF_INET;
    strncpy(ifr.ifr_name , iface , IFNAMSIZ-1);

    if (0 == ioctl(fd, SIOCGIFHWADDR, &ifr)) {
        unsigned char *mac = (unsigned char *)ifr.ifr_hwaddr.sa_data;
		
        
        sprintf(addr, "%.2X%.2X%.2X%.2X%.2X%.2X\n" , mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    }
	else {printf ("MAC ADDR ERROR!!!");}
	
    close(fd);
}