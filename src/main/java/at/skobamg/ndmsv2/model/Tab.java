package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.logic.IntDataExtractorCommand;
import at.skobamg.ndmsv2.logic.IntStatusExtractorCommand;
import at.skobamg.ndmsv2.logic.StringParser;
import at.skobamg.ndmsv2.logic.TabsContoller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class Tab implements ITab{
	private String tabName;
	private StringProperty consoleText = new SimpleStringProperty();	
	private ArrayList<IInterface> interfaces;
	private ConsoleOutput consoleOutput;
	private PipedOutputStream consoleInput;
	private ArrayList<String> templates;
	private ITemplate template;
	private ISnippet interfaceSnippet;
	private TabSession tabSession;
	private TabService tabService;
	
	public Tab(String tabName, ArrayList<IInterface> interfaces,
			String[] sessionInfo, ArrayList<String> templates, ITemplate template){
		super();
		this.tabName = tabName;
		this.interfaces = interfaces;
		this.templates = templates;
		this.template = template;					
		tabSession = new TabSession(sessionInfo[0], sessionInfo[1], sessionInfo[2], sessionInfo[3]);
		
		for(ISnippet snippet : template.getSnippets())
			if(snippet.isBindInterface()) setInterfaceSnippet(snippet);		
		Thread thread = new Thread((tabService = new TabService()));
		thread.start();
	}

	public final String getConsoleText() { return consoleText.get(); }
	
	public StringProperty consoleTextProperty() {
		return consoleText;
	}
	
	public ITemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(ITemplate template) {
		this.template = template;
	}
	
	public String getTabName() {
		return tabName;
	}
	
	public ArrayList<IInterface> getInterfaces() {
		return interfaces;
	}
	
	public ArrayList<String> getTemplates() {
		return templates;
	}
	
	
	@Override
	public void sendCommand(String command){
		try {
			consoleInput.write((command+"\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1,
			String arg2) {
		if( (arg2.endsWith(tabName+">") || arg2.endsWith(tabName+"#")) && !arg2.equals(arg1)) {
			consoleText.set(consoleText.get()+"\n"+arg2);
		}			
	}

	@Override
	public ConsoleOutput getConsoleOutput() {
		return consoleOutput;
	}

	public ISnippet getInterfaceSnippet() {
		return interfaceSnippet;
	}

	public void setInterfaceSnippet(ISnippet interfaceSnippet) {
		this.interfaceSnippet = interfaceSnippet;
	}
	
	class TabSession {
		private String host;
		private String username;
		private String password;
		private String secret;		
		
		public TabSession(String host, String username, String password,
				String secret) {
			super();
			this.host = host;
			this.username = username;
			this.password = password;
			this.secret = secret;			
		}
	}
	
	class TabService implements Runnable{
		IntStatusExtractorCommand intStatusExtractorCommand;
		IntDataExtractorCommand intDataExtractorCommand;
		ConsoleOutput serviceConsoleOutput;
		PipedOutputStream serviceConsoleInput;
		Session session;
		Channel channel;
		boolean startDataExtraction;
		boolean stop;
		long refresh_rate = TabsContoller.TAB_NORMAL_PRIORITY;		
		
		void sendCommand(String command) {
			try {
				serviceConsoleInput.write((command+"\n").getBytes());
			} catch (IOException e) {
				session.disconnect();
				startConnection();
				sendCommand(command);
			}
		}
		
		@Override
		public void run() {
			while(!stop) {
				if(session == null || !session.isConnected()) {
					startConnection();
				}
				else if(startDataExtraction) {	
					try {
						Thread.sleep(refresh_rate);
					} catch (InterruptedException e) {}
					//Reset the output, send the command and wait for an answer
					serviceConsoleOutput.resetOutput();
					sendCommand("show running-config");					
					while(!serviceConsoleOutput.getConsoleText().endsWith(tabName+"#") || serviceConsoleOutput.getConsoleText().split("\n").length < 10);
								
					intDataExtractorCommand = new IntDataExtractorCommand(serviceConsoleOutput.getConsoleText(), interfaceSnippet, interfaces);
					intDataExtractorCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent arg0) {
							new Thread(new Runnable() {								
								@Override
								public void run() {
									serviceConsoleOutput.resetOutput();
									sendCommand("show ip interface brief");								
									while(!serviceConsoleOutput.getConsoleText().endsWith(getTabName()+"#") || serviceConsoleOutput.getConsoleText().split("\n").length < 10);
									intStatusExtractorCommand = new IntStatusExtractorCommand(interfaces, StringParser.extractInterfaceInformation(template.getInterfaces(), serviceConsoleOutput.getConsoleText(), tabName));
									intStatusExtractorCommand.setOnSucceeded(new CustomHandler());
									intStatusExtractorCommand.setOnFailed(new CustomHandler());
									intStatusExtractorCommand.start();
								}
							}).start();							
						}
					});	
					intDataExtractorCommand.setOnFailed(new CustomHandler());
					startDataExtraction = false;
					intDataExtractorCommand.start();
				}
							
			}
		}
			
		private void startConnection() {
			//Create a new channel to start sending commands through		
			try {
				JSch jSch = new JSch();
				session = jSch.getSession(tabSession.username, tabSession.host);
				session.setPassword(tabSession.password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				
				channel = session.openChannel("shell");		
				InputStream in = new PipedInputStream();				
				serviceConsoleInput = new PipedOutputStream((PipedInputStream)in );
				serviceConsoleOutput = new ConsoleOutput();
				channel.setInputStream(in, true);
				channel.setOutputStream(serviceConsoleOutput);
				channel.connect();
				
				sendCommand("enable");					
				sendCommand(tabSession.secret);
				sendCommand("terminal length 0\n");
				
				startDataExtraction = true;
				
			}
			 catch (JSchException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		class CustomHandler implements EventHandler<WorkerStateEvent>{

			@Override
			public void handle(WorkerStateEvent event) {
				serviceConsoleOutput.resetOutput();
				startDataExtraction = true;
			}
			
		}
	}

//		tabService.stopExecution();
//		Thread thread = new Thread((tabService = new TabService(refresh_rate)));
//		thread.start();
	
}
