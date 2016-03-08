


	
void SetFlowCommandsQueue(FlowQueue fq) {
	gData->FlowCommandsQueue = fq;
}

void SetControlEventsQueue(FlowQueue fq) {
	gData->ControlEventsQueue = fq;
}

void SetFlowControlThread(FlowThread ft) {
	gData->FlowControlThread = ft;
}

void SetControllerThread(FlowThread ft) {
	gData->ContollerThread = ft;
}

void SetMessagaingService(FlowService fs) {
	gData->Service = fs;
}

void SetFlowID(char* fid) {
	strcpy(gData->FlowID, fid);
}

FlowQueue GetFlowCommandsQueue() {
	return gData->FlowCommandsQueue;
}

FlowQueue GetControlEventsQueue() {
	return gData->ControlEventsQueue;
}

FlowThread GetFlowControlThread() {
	return gData->FlowControlThread;
}

FlowThread GetControllerThread() {
	return gData->ContollerThread;
}

FlowService GetMessagaingService() {
	return gData->Service;
}

void GetFlowID(char* buff) {
	strcpy(buff, gData->FlowID);
}				
				
	
	
