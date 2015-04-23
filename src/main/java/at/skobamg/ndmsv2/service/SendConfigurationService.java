package at.skobamg.ndmsv2.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import at.skobamg.ndmsv2.model.ConsoleOutput;
import at.skobamg.ndmsv2.model.Tab.TabSession;

public class SendConfigurationService implements Runnable {
	private String username;
	private String host;	
	private String password;
	private String secret;
	private String commands;
	private PipedOutputStream consoleInput;
	private Session session;
	private Channel channel;
	private ConsoleOutput consoleOutput = new ConsoleOutput();
	
	public SendConfigurationService(String commands, TabSession tabSession) {
		this.commands = commands;
		host = tabSession.getSessionData()[0];
		username = tabSession.getSessionData()[1];
		password = tabSession.getSessionData()[2];
		secret = tabSession.getSessionData()[3];
	}
	
	public ConsoleOutput getConsoleOutput() {
		return consoleOutput;
	}
	
	@Override
	public void run() {
		startConnection();
			for(String command : commands.split("\n"))
				sendCommand(command+"\n");
		channel.disconnect();
		session.disconnect();
	}	
	
	private void startConnection() {
		//Create a new channel to start sending commands through		
		try {
			JSch jSch = new JSch();
			session = jSch.getSession(username, host);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			
			channel = session.openChannel("shell");		
			InputStream in = new PipedInputStream();				
			consoleInput = new PipedOutputStream((PipedInputStream)in );
			channel.setInputStream(in, true);
			channel.setOutputStream(consoleOutput);
			channel.connect();
			
			sendCommand("enable\n");
			sendCommand(secret+"\n");
			sendCommand("terminal length 0\n");
			sendCommand("configure terminal\n");
		}
		 catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void sendCommand(String command) {
		try {
			consoleInput.write(command.getBytes());
			Thread.sleep(500);
		} catch (IOException | InterruptedException e) {
			
		}
	}
	
}
