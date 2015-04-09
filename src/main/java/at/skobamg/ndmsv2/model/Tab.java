package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.jcraft.jsch.Channel;

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
	private ArrayList<String> templates;
	private ITemplate template;
	private ISnippet interfaceSnippet;
	private volatile boolean lockConsole;
	private Timer timer;
	private Channel shellChannel;
	
	public Tab(String tabName, ArrayList<IInterface> interfaces, ConsoleOutput consoleOutput, Channel shellChannel,
			ArrayList<String> templates, ITemplate template) {
		super();
		this.tabName = tabName;
		this.interfaces = interfaces;
		this.shellChannel = shellChannel;
		this.consoleOutput = consoleOutput;
		this.templates = templates;
		this.template = template;
		timer = new Timer(tabName);
		
		//Starting logic
		consoleOutput.resetOutput();
		consoleOutput.consoleTextProperty().addListener(this);
		for(ISnippet snippet : template.getSnippets())
			if(snippet.isBindInterface()) setInterfaceSnippet(snippet);
		timer.scheduleAtFixedRate(new TabService(), TabsContoller.TAB_WAIT_PERIOD, TabsContoller.TAB_WAIT_PERIOD);
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
	
	public boolean isLockConsole() {
		return lockConsole;
	}
	
	public void setLockConsole(boolean lockConsole) {
		this.lockConsole = lockConsole;
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
			if(!lockConsole) {
				shellChannel.getInputStream().close();
				new PipedOutputStream((PipedInputStream)shellChannel.getInputStream()).write((command+"\n").getBytes());
			}
//				consoleInput.write((command+"\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1,
			String arg2) {
		if(lockConsole) return;
		else if( (arg2.endsWith(tabName+">") || arg2.endsWith(tabName+"#")) && !arg2.equals(arg1)) {
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
	
	class TabService extends TimerTask{
		private boolean startDataExtraction = true;
		IntStatusExtractorCommand intStatusExtractorCommand;
		IntDataExtractorCommand intDataExtractorCommand;
		
		@Override
		public boolean cancel() {
			if(intDataExtractorCommand.isRunning())
				intDataExtractorCommand.cancel();
			if(intStatusExtractorCommand.isRunning())
				intStatusExtractorCommand.cancel();
			getConsoleOutput().resetOutput();
			setLockConsole(false);
			return super.cancel();
		}
		
		@Override
		public void run() {
			if(startDataExtraction && getConsoleOutput().getConsoleText().isEmpty() && !isLockConsole()){
				consoleOutput.resetOutput();
				sendCommand("show running-config");
				setLockConsole(true);		
				while(!getConsoleOutput().getConsoleText().endsWith(getTabName()+">") && !getConsoleOutput().getConsoleText().endsWith(getTabName()+"#"));
				startDataExtraction = false;
				intDataExtractorCommand = new IntDataExtractorCommand(consoleOutput.getConsoleText(), interfaceSnippet, interfaces);
				intDataExtractorCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent arg0) {
						getConsoleOutput().resetOutput();
						setLockConsole(false);			
						while(!getConsoleOutput().getConsoleText().isEmpty() || isLockConsole());								
						getConsoleOutput().resetOutput();
						sendCommand("show ip interface brief");								
						setLockConsole(true);
						while(!getConsoleOutput().getConsoleText().endsWith(getTabName()+">") && !getConsoleOutput().getConsoleText().endsWith(getTabName()+"#"));
						intStatusExtractorCommand = new IntStatusExtractorCommand(interfaces, StringParser.extractInterfaceInformation(template.getInterfaces(), getConsoleOutput().getConsoleText(), tabName));
						intStatusExtractorCommand.setOnSucceeded(new CustomHandler());
						intStatusExtractorCommand.setOnFailed(new CustomHandler());
						intStatusExtractorCommand.start();
					}
				});	
				intDataExtractorCommand.setOnFailed(new CustomHandler());
				intDataExtractorCommand.start();
				
			}
		}
		
		class CustomHandler implements EventHandler<WorkerStateEvent>{

			@Override
			public void handle(WorkerStateEvent event) {
				getConsoleOutput().resetOutput();
				setLockConsole(false);								
				startDataExtraction = true;
			}
			
		}
		
	}

	@Override
	public void refreshTab(long refresh_rate) {
		timer.cancel();
		timer.scheduleAtFixedRate(new TabService(), refresh_rate, refresh_rate);
	}

	public Channel getShellChannel() {
		return shellChannel;
	}
	
}
