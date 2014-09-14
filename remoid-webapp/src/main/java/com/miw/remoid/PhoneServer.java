package com.miw.remoid;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class PhoneServer {
	private PrintWriter updater; 
	
	public void update(BrowserRequest req) throws JsonProcessingException {
		String jsonStr = Application.OBJECT_MAPPER.writeValueAsString(req);
		
		updater.println(jsonStr);
		updater.println("EOF");
	}
	
	@PostConstruct
	public void postConstruct() {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(8082);
					System.out.println("created server");

					Socket socket = serverSocket.accept();
					System.out.println("connection accepted: " + socket.toString());

					updater = new PrintWriter(socket.getOutputStream(), true);
				} catch (IOException e) {
				}
			}
		});

		t.start();
	}
}
