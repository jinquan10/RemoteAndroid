package com.miw.remoid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	@Autowired
	private PhoneServer phoneServer;
	
	@MessageMapping("/update")
	public void update(BrowserRequest message) throws Exception {
		phoneServer.update(message);
	}
}
