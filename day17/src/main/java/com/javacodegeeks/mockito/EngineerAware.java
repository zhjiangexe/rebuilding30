package com.javacodegeeks.mockito;

public interface EngineerAware {
	Engineer getDesignation();
	
	enum Engineer {
		DEV,QA
	}
}
