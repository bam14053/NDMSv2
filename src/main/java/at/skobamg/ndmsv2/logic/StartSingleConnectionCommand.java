package at.skobamg.ndmsv2.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.generator.model.InvalidTypeException;
import at.skobamg.generator.service.TemplateService;
import at.skobamg.ndmsv2.model.ConsoleOutput;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITemplateCollection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class StartSingleConnectionCommand extends Service<Void> {
	private String host, username, password, secret;
	private ITabsController tabsContoller;
	private ITemplateCollection templateCollection;
	private ConsoleOutput consoleOutput;
	private PipedOutputStream consoleInput;
	private String hostname;
	
	public StartSingleConnectionCommand(ITabsController tabsContoller, ITemplateCollection templateCollection, 
			String host, String username, String password, String secret) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
		this.secret = secret;
		this.tabsContoller = tabsContoller;
		this.templateCollection = templateCollection;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				JSch jSch = new JSch();
				Session session = jSch.getSession(username, host);
				session.setPassword(password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				//Extracting the required streams from channel
				updateMessage("Connected to host: "+host);
				updateProgress(0.2, 1);
				
				Channel channel = session.openChannel("shell");				
				InputStream in = new PipedInputStream();				
				consoleInput = new PipedOutputStream((PipedInputStream)in );
				consoleOutput = new ConsoleOutput();
				channel.setInputStream(in);
				channel.setOutputStream(consoleOutput);
				channel.connect();
				//starting the initialization process
				String command = "enable\n";
				sendCommand(command);
				
				command = secret+"\n";				
				sendCommand(command);
				sendCommand("terminal length 0\n");
				
				do{
					Thread.sleep(ConsoleOutput.consoleWaitPeriod);	
				}while(consoleOutput.getConsoleText().isEmpty());		
				hostname = StringParser.extractHostname(consoleOutput.getConsoleText());
				consoleOutput.resetOutput();

				//update progress		
				updateMessage("Loading templates for host "+hostname);
				updateProgress(0.4, 1);
				//Next part, version extraction
				command = "show version\n";
				sendCommand(command);							
				while(consoleOutput.getConsoleText().isEmpty() || !consoleOutput.getConsoleText().contains("cisco"))
					Thread.sleep(ConsoleOutput.consoleWaitPeriod);									

				updateProgress(0.6, 1);

				//Load templates and interfaces
				ArrayList<String> templates = templateCollection.getTemplatesofSwitch(StringParser.extractSwitchName(consoleOutput.getConsoleText(), hostname), 
						StringParser.extractIOSVersion(consoleOutput.getConsoleText(), hostname));									
				if(templates.isEmpty()) 
					throw new Exception("No corresponding template could be loaded");				
				
				//Templates extraction sucessful
				updateMessage("Version extraction and templates loaded");
				updateProgress(0.8, 1);
				try {
					ITemplate template = new TemplateService().openTemplate(new File(templates.get(0)));
					ArrayList<IInterface> interfaces = extractInterfaces(template);
					updateMessage("Interface information loaded into program");
					updateProgress(1, 1);
					tabsContoller.addTab(hostname, interfaces, consoleOutput, channel, templates, template);
				} catch (ParserConfigurationException | SAXException | IOException e) {
					throw new Exception("Error parsing template, please check whether the template is valid");
				} catch (InvalidTypeException e) {
					throw new Exception ("Invalid parameter type in the template, error message: "+e.getMessage());
				}								
				return null;
			}
			
			ArrayList<IInterface> extractInterfaces(ITemplate template) throws Exception{	
				consoleOutput.resetOutput();
				sendCommand("\nshow ip interface brief\r");
				while(consoleOutput.getConsoleText().isEmpty() || !consoleOutput.getConsoleText().contains("Interface"))
					Thread.sleep(ConsoleOutput.consoleWaitPeriod);	
				
				return StringParser.extractInterfaceInformation(template.getInterfaces(), consoleOutput.getConsoleText(), hostname);
				
			}

			void sendCommand(String command) throws Exception{
				try {
					consoleInput.write(command.getBytes());
				} catch (IOException e) {
					throw new Exception("An error occured while sending following command to switch: "+ command);
				}
			}									
		};
	}

}
