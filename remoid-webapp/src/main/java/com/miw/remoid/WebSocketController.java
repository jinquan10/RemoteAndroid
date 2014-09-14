package com.miw.remoid;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	@MessageMapping("/update")
	public void update(BrowserRequest message) throws Exception {
		
	}
}
