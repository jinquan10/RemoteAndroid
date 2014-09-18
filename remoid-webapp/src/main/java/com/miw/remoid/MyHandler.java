package com.miw.remoid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {
	private Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		
		sessions.put(session.getRemoteAddress().getAddress().getHostAddress(), session);
		
		while(true) {
			session.sendMessage(new TextMessage("hi123123"));
		}
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
	}
}