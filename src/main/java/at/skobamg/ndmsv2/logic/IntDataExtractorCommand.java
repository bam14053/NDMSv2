package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;

import at.skobamg.generator.model.ICommand;
import at.skobamg.generator.model.IParameter;
import at.skobamg.generator.model.ISection;
import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.IViewElement;
import at.skobamg.ndmsv2.model.IInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class IntDataExtractorCommand extends Service<Void> {
	private String consoleText;
	private ICommand intCommand;
	private ArrayList<IInterface> interfaces;
	private ISnippet interfaceSnippet;
	
	public IntDataExtractorCommand(String consoleText, ISnippet interfaceSnippet,
			ArrayList<IInterface> interfaces) {
		super();
		this.consoleText = consoleText;
		this.interfaces = interfaces;
		this.interfaceSnippet = interfaceSnippet;
		intCommand = interfaceSnippet.getSections().get(0).getCommands().get(0);
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String[] lines = consoleText.replace("\r", "").toLowerCase().split("\n");		
				for(int i2 = 0; i2 < interfaces.size(); i2++) {
					String intData = "";
					for(int i = 0; i < lines.length; i++) {
						if(lines[i].equalsIgnoreCase(intCommand.getExeccommand()+" "+interfaces.get(i2).getPortnameLong())) {			
							while(!lines[++i].equals("!"))
								intData += "\t"+lines[i]+"\n"; 
							if(!intData.isEmpty()) interfaces.get(i2).setRunningConfig(intCommand.getExeccommand()+" "+interfaces.get(i2).getPortnameLong()+"\n"+intData);
							for(String data : intData.split("\n")) {
								if(!data.isEmpty())
									for(ISection section : interfaceSnippet.getSections()) {
										for(ICommand command : section.getCommands()) {
											ICommand foundCommand;
											if( (foundCommand = isCommandPresent(command, data.trim())) != null)
												interfaces.get(i2).addInterfaceData(foundCommand.getName(), data.replace(foundCommand.getExeccommand(), "").trim());
										}
									}								
							} 
							break;
						}
					}
				}				
				return null;
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
		};
	}

}
