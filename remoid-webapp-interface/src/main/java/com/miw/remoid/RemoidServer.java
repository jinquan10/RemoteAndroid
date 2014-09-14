package com.miw.remoid;

public interface RemoidServer {
	String prodHost = "ws://localhost:8081";
	
	void connect(String host);
}
