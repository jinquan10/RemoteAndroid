package com.miw.remoid;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	@MessageMapping("/update")
	@SendTo("/topic/greetings")
	public void greeting(BrowserRequest message) throws Exception {
		System.out.println(message.getName());
	}
}
