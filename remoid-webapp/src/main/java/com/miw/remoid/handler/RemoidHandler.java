package com.miw.remoid.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.miw.remoid.OperationMapper;
import com.miw.remoid.PhoneDimension;
import com.miw.remoid.WSRequest;
import com.miw.remoid.util.Singletons;

public class RemoidHandler extends TextWebSocketHandler {
	private Logger logger = LoggerFactory.getLogger(RemoidHandler.class);

	private Set<WebSocketSession> browserSessions = new HashSet<WebSocketSession>();
	private Map<WebSocketSession, PhoneDimension> phoneSessions = new HashMap<WebSocketSession, PhoneDimension>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);

		List<String> protocol = session.getHandshakeHeaders().get("sec-websocket-protocol");

		if (protocol == null) {
			return;
		}

		PhoneDimension phoneDimension = Singletons.OBJECT_MAPPER.readValue(protocol.get(0), PhoneDimension.class);

		phoneSessions.put(session, phoneDimension);
		WSRequest phoneDimensionsForBrowser = updatePhoneDimensions(OperationMapper.UPDATE_PHONE_CONNECTION);

		for (WebSocketSession browserSession : browserSessions) {
			browserSession.sendMessage(new TextMessage(Singletons.OBJECT_MAPPER.writeValueAsString(phoneDimensionsForBrowser)));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);

		List<String> protocol = session.getHandshakeHeaders().get("sec-websocket-protocol");

		if (protocol == null) { // browser since javascript is conflicting with spring's websocket impl.  it is not returning socket header protocol
			browserSessions.remove(session);
		} else {
			phoneSessions.remove(session);
			WSRequest request = updatePhoneDimensions(OperationMapper.UPDATE_PHONE_CONNECTION);

			for (WebSocketSession browserSession : browserSessions) {
				browserSession.sendMessage(new TextMessage(Singletons.OBJECT_MAPPER.writeValueAsString(request)));
			}
		}
	}

	// write phoneDisconnect here, and browser disconnect too

	private WSRequest updatePhoneDimensions(int operation) {
		List<PhoneDimension> phoneDimensions = new ArrayList<PhoneDimension>();
		for (Entry<WebSocketSession, PhoneDimension> entry : phoneSessions.entrySet()) {
			phoneDimensions.add(entry.getValue());
		}

		WSRequest request = new WSRequest();
		request.setOp(operation);
		request.setPhoneDimensions(phoneDimensions);
		return request;
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			WSRequest request = Singletons.OBJECT_MAPPER.readValue(message.getPayload(), WSRequest.class);

			if (request.getOp() == OperationMapper.BROWSER_CONNECT) {
				browserSessions.add(session);

				request = updatePhoneDimensions(OperationMapper.UPDATE_PHONE_CONNECTION);
				session.sendMessage(new TextMessage(Singletons.OBJECT_MAPPER.writeValueAsString(request)));
			} else {
				// - just do one phone for now
				WebSocketSession phoneSession = null;
				
				for (WebSocketSession ps : phoneSessions.keySet()) {
					if (ps != null) {
						phoneSession = ps;
						break;
					}
				}
				
				phoneSession.sendMessage(message);
			}
		} catch (IOException e) {
			logger.error("unable to deserialize message", e);
		}
	}
}