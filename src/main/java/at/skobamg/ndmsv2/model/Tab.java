package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import at.skobamg.generator.model.ICommand;
import at.skobamg.generator.model.IParameter;
import at.skobamg.generator.model.ISection;
import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.generator.model.IViewElement;
import at.skobamg.ndmsv2.logic.IntDataExtractorCommand;
import at.skobamg.ndmsv2.logic.IntStatusExtractorCommand;
import at.skobamg.ndmsv2.logic.StringParser;
import at.skobamg.ndmsv2.logic.TabsContoller;
import at.skobamg.ndmsv2.service.SendConfigurationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class Tab implements ITab{
	private String tabName;
	private StringProperty consoleText = new SimpleStringProperty();	
	private ArrayList<IInterface> interfaces;
	private ArrayList<String> templates;
	private ITemplate template;
	private ISnippet interfaceSnippet;
	private HashMap<String, String> tabData = new HashMap<String, String>();
	private TabSession tabSession;
	private TabInterfacesService tabInterfacesService;
	private TabDataService tabDataService;
	
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
		new Thread((tabInterfacesService = new TabInterfacesService())).start();
		new Thread((tabDataService) = new TabDataService()).start();
	}

	public final String getConsoleText() { return consoleText.get(); }
	
	public StringProperty consoleTextProperty() {
		return consoleText;
	}
	
	@Override
	public ITemplate getTemplate() {
		return template;
	}
	
	@Override
	public void setTemplate(ITemplate template) {
		this.template = template;
	}
	
	@Override
	public String getTabName() {
		return tabName;
	}
	
	@Override
	public ArrayList<IInterface> getInterfaces() {
		return interfaces;
	}
	
	@Override
	public ArrayList<String> getTemplates() {
		return templates;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
		if( (arg2.endsWith(tabName+">") || arg2.endsWith(tabName+"#")) && !arg2.equals(arg1)) {
			consoleText.set(consoleText.get()+arg2+"\n");
		}
	}
	
	@Override
	public ISnippet getInterfaceSnippet() {
		return interfaceSnippet;
	}

	@Override
	public void setInterfaceSnippet(ISnippet interfaceSnippet) {
		this.interfaceSnippet = interfaceSnippet;
	}

	public class TabSession {
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
		
		public String[] getSessionData() {
			return new String[]{
				host, username, password, secret
			};
		}
	}
	
	class TabDataService implements Runnable{
		String consoleText;
		ArrayList<ISnippet> dataSnippets = new ArrayList<ISnippet>();
		
		public TabDataService() {
			for(ISnippet snippet : template.getSnippets()) {
				if(snippet.isBindInterface()) break;
				else dataSnippets.add(snippet);
			}
		}
		
		@Override
		public void run() {
			while(true) {
				if(consoleText != null) {
					tabData.clear();
					String[] lines = consoleText.replace("\r", "").toLowerCase().split("\n");
					for(int i = 0; i < lines.length; i++) 
						for(ISnippet snippet : dataSnippets) 
							for(ISection section : snippet.getSections())
								for(ICommand command : section.getCommands()) {
									ICommand foundCommand;
									if( (foundCommand = isCommandPresent(command, lines[i].trim())) != null)
										tabData.put(foundCommand.getName(), lines[i].replace(foundCommand.getExeccommand(), "").trim());
								}
					try { Thread.sleep(TabsContoller.TAB_MINIMAL_PRIORITY);} catch (InterruptedException e) {}
				}
			}
		}
		
		private ICommand isCommandPresent(IViewElement command, String line) {
			switch (command.getViewTyp()) {
			case ICommand:		
				if(line.contains(((ICommand)command).getExeccommand()))
					return (ICommand) command;
				for(ICommand command2 : ((ICommand)command).getCommands())
					if(isCommandPresent(command2, line)!=null) return command2;
				for(IParameter parameter : ((ICommand)command).getParameters()) {
					ICommand command2 = isCommandPresent(parameter, line);
					if(command2 != null) return command2;
				}
				break;
			case IParameter:
				for(ICommand command2 : ((IParameter)command).getCommands())
					if(isCommandPresent(command2, line) != null) return command2;
				for(IParameter parameter : ((IParameter)command).getParameters()) {
					ICommand command2 = isCommandPresent(parameter, line);
					if(command2 != null) return command2;
				}
				break;
			default:
				break;
			}
			return null;
		}
		
		public void setConsoleText(String consoleText) {
			this.consoleText = consoleText;
		}
		
	}
	
	class TabInterfacesService implements Runnable{
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
					sendCommand("show running-config full");					
					while(!serviceConsoleOutput.getConsoleText().endsWith(tabName+"#") || serviceConsoleOutput.getConsoleText().split("\n").length < 10);
								
					tabDataService.setConsoleText(serviceConsoleOutput.getConsoleText());
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

	@Override
	public HashMap<String, String> getTabData() {
		return tabData;
	}

	@Override
	public TabSession getTabSession() {
		return tabSession;
	}

	@Override
	public void sendCommandsToSwitch(String commands) {
		SendConfigurationService sendConfigurationService = new SendConfigurationService(tabSession, commands);
		sendConfigurationService.getConsoleOutput().consoleTextProperty().addListener(this);
		new Thread(sendConfigurationService).start();
	}

	

//		tabService.stopExecution();
//		Thread thread = new Thread((tabService = new TabService(refresh_rate)));
//		thread.start();
	
}
