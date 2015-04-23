/**
 * 
 */
package at.skobamg.ndmsv2.logic;
import java.util.ArrayList;
import java.util.HashMap;
import at.skobamg.generator.model.ICommand;
import at.skobamg.generator.model.IParameter;
import at.skobamg.generator.model.ISection;
import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.view.RestrictiveTextField;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author abideen
 *
 */
public class GenerateCommandHierachyCommand extends Service<TreeItem<Node>> {
	private ArrayList<ISnippet> snippets;
	private String name;
	private IInterface interf;
	private HashMap<String, String> tabData;
	
	public GenerateCommandHierachyCommand(ITemplate template, HashMap<String, String> tabData) {
		snippets = new ArrayList<ISnippet>(template.getSnippets());
		snippets.removeIf((snippet)->snippet.isBindInterface());
		name = template.getSwitchName()+" "+template.getSwitchVersion();
		this.tabData = tabData;
	} 
	
	public GenerateCommandHierachyCommand(IInterface interf, ISnippet snippet) {
		snippets = new ArrayList<ISnippet>();
		snippets.add(snippet);
		this.interf = interf;
		this.name = interf.getPortnameLong();
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<TreeItem<Node>> createTask() {
		// TODO Auto-generated method stub
		return new Task<TreeItem<Node>>() {

			@Override
			protected TreeItem<Node> call() throws Exception {
				Text title = new Text(name);
				if(interf == null)
					title.setUserData(false);
				else
					title.setUserData(true);
				TreeItem<Node> treeitems = new TreeItem<Node>(title);
				for(ISnippet snippet : snippets) {
					TreeItem<Node> snippetText = new TreeItem<Node>(new Text(snippet.getName()));
					((Text)snippetText.getValue()).setFill(Color.RED);
					for(ISection section : snippet.getSections()) {
						TreeItem<Node> sectionText = new TreeItem<Node>(new Text(section.getName()));
						((Text)sectionText.getValue()).setFill(Color.DARKBLUE);
						snippetText.getChildren().add(sectionText);
						for(ICommand command : section.getCommands()) {																	
							TreeItem<Node> commandNode = getCommandNode(command);							
							sectionText.getChildren().add(commandNode);
						}						
					}
					treeitems.getChildren().add(snippetText);
				}
				return treeitems;
			}

			protected void addAllParameters(IParameter parameter, HBox hbox) {
				for(IParameter parameter2 : parameter.getParameters()) {
					if(parameter2.getType() != null) {
						switch (parameter2.getType()) {
						case Bool:
							CheckBox checkBox = new CheckBox(parameter2.getName());
							if(interf != null && interf.getInterfaceData(parameter2.getName()) != null && interf.getInterfaceData(parameter2.getName()).isEmpty())
								checkBox.setSelected(true);
							else if(tabData != null && tabData.get(parameter2.getName()) != null && tabData.get(parameter2.getName()).equals("no"))
								checkBox.setSelected(true);
							checkBox.setUserData(parameter2); //USER DATA
							hbox.getChildren().add(checkBox);
							break;
						case Integer:
							RestrictiveTextField restrictiveTextField = new RestrictiveTextField();
							if(interf != null && interf.getInterfaceData(parameter2.getName()) != null)
								restrictiveTextField.setText(interf.getInterfaceData(parameter2.getName()));
							else if(tabData != null && tabData.get(parameter2.getName()) != null)
								restrictiveTextField.setText(tabData.get(parameter2.getName()));
							restrictiveTextField.setPromptText(parameter2.getName());
							restrictiveTextField.setRestrict("[0-9]");
							restrictiveTextField.setUserData(parameter2); //USER DATA
							hbox.getChildren().add(restrictiveTextField);
							break;
						case String:
							TextField param = new TextField();		
							if(interf != null && interf.getInterfaceData(parameter2.getName()) != null)
								param.setText(interf.getInterfaceData(parameter2.getName()));
							else if(tabData != null && tabData.get(parameter2.getName()) != null)
								param.setText(tabData.get(parameter2.getName()));
							param.setPromptText(parameter2.getName());
							param.setUserData(parameter2); //USER DATA
							hbox.getChildren().add(param);
							break;
						case Delim:
							RestrictiveTextField delim = new RestrictiveTextField();
							delim.setPromptText(parameter2.getName());							
							delim.setMaxLength(1);
							delim.setUserData(parameter2); //USER DATA
							hbox.getChildren().add(delim);
						default:
							break;
						}
					}else {
						switch (parameter.getType()) {
						case Bool:
							CheckBox checkBox = new CheckBox(parameter.getName());
							if(interf != null && interf.getInterfaceData(parameter.getName()) != null && interf.getInterfaceData(parameter.getName()).isEmpty())
								checkBox.setSelected(true);
							else if(tabData != null && tabData.get(parameter.getName()) != null && tabData.get(parameter.getName()).equals("no"))
								checkBox.setSelected(true);
							checkBox.setUserData(parameter); //USER DATA
							hbox.getChildren().add(checkBox);
							break;
						case Integer:
							RestrictiveTextField restrictiveTextField = new RestrictiveTextField();
							if(interf != null && interf.getInterfaceData(parameter.getName()) != null)
								restrictiveTextField.setText(interf.getInterfaceData(parameter.getName()));
							else if(tabData != null && tabData.get(parameter.getName()) != null)
								restrictiveTextField.setText(tabData.get(parameter.getName()));
							restrictiveTextField.setPromptText(parameter.getName());
							restrictiveTextField.setRestrict("[0-9]");
							restrictiveTextField.setUserData(parameter); //USER DATA
							hbox.getChildren().add(restrictiveTextField);
							break;
						case String:
							TextField param = new TextField();		
							if(interf != null && interf.getInterfaceData(parameter.getName()) != null)
								param.setText(interf.getInterfaceData(parameter.getName()));
							else if(tabData != null && tabData.get(parameter.getName()) != null)
								param.setText(tabData.get(parameter.getName()));
							param.setPromptText(parameter.getName());
							param.setUserData(parameter); //USER DATA
							hbox.getChildren().add(param);
							break;
						case Delim:
							RestrictiveTextField delim = new RestrictiveTextField();
							delim.setPromptText(parameter.getName());
							delim.setMaxLength(1);				
							delim.setUserData(parameter); //USER DATA
							hbox.getChildren().add(delim);
						default:
							break;
						}
					}
					addAllParameters(parameter2, hbox);
				}
			}
			
			
			protected TreeItem<Node> getCommandNode(ICommand command) {
				TreeItem<Node> commandNode = null;				
				if(command.getParameters().isEmpty() || command.getParameters().size() == 1) {	
					HBox hbox = new HBox(10);							
					hbox.getChildren().add(new Text(command.getName()));
					hbox.setUserData(command); //USER DATA!!!
					commandNode = new TreeItem<Node>(hbox);
					switch(command.getType()) {
					case Bool:
						CheckBox checkBox = new CheckBox();
						if(interf != null && interf.getInterfaceData(command.getName()) != null && interf.getInterfaceData(command.getName()).isEmpty())
								checkBox.setSelected(true);
						else if(tabData != null && tabData.get(command.getName()) != null && tabData.get(command.getName()).equals("no"))
								checkBox.setSelected(true);							
						hbox.getChildren().add(checkBox);
						break;
					case Integer:
						RestrictiveTextField restrictiveTextField = new RestrictiveTextField();
						if(interf != null && interf.getInterfaceData(command.getName()) != null)
							restrictiveTextField.setText(interf.getInterfaceData(command.getName()));
						else if(tabData != null && tabData.get(command.getName()) != null)
							restrictiveTextField.setText(tabData.get(command.getName()));
						if(!command.getParameters().isEmpty())
							restrictiveTextField.setPromptText(command.getParameters().get(0).getName());
						restrictiveTextField.setRestrict("[0-9]");
						hbox.getChildren().add(restrictiveTextField);
						break;
					case String:
						TextField param = new TextField();	
						if(interf != null) {
							if(interf.getInterfaceData(command.getName()) != null)
								param.setText(interf.getInterfaceData(command.getName()));
							if(command.getExeccommand().equals("interface"))
								param.setText(interf.getPortnameLong());
						}
						else if(tabData != null && tabData.get(command.getName()) != null)
							param.setText(tabData.get(command.getName()));
						if(!command.getParameters().isEmpty())
							param.setPromptText(command.getParameters().get(0).getName());
						hbox.getChildren().add(param);
						break;
					default:
						break;											
					}
				}else{
					Text text = new Text(command.getName());
					text.setUserData(command);
					commandNode = new TreeItem<Node>(text);								
					switch (command.getType()) {
					case Choice:
						ToggleGroup toggleGroup = new ToggleGroup();
						for(IParameter parameter : command.getParameters()) {
							HBox hbox = new HBox(10);
							hbox.setUserData(parameter);//USER DATA
							
							RadioButton radioButton = new RadioButton(parameter.getName());
							radioButton.setToggleGroup(toggleGroup);
							radioButton.setUserData(hbox);
							
							hbox.getChildren().add(radioButton);
							addAllParameters(parameter, hbox);
							if(parameter.getCommands().isEmpty())
								commandNode.getChildren().add(new TreeItem<Node>(hbox));
							else {
								TreeItem<Node> parameterNode = new TreeItem<Node>(hbox);
								for(ICommand command2 : parameter.getCommands())
									parameterNode.getChildren().add(getCommandNode(command2));
								commandNode.getChildren().add(parameterNode);
							}
						}
						break;
					case Multi:
						for(IParameter parameter: command.getParameters()) {
							HBox hbox = new HBox(10);
							hbox.setUserData(parameter);//USER DATA
							
							CheckBox checkBox = new CheckBox(parameter.getName());
							hbox.getChildren().add(checkBox);
							
							addAllParameters(parameter, hbox);
							if(parameter.getCommands().isEmpty())
								commandNode.getChildren().add(new TreeItem<Node>(hbox));
							else {
								TreeItem<Node> parameterNode = new TreeItem<Node>(hbox);
								for(ICommand command2 : parameter.getCommands())
									parameterNode.getChildren().add(getCommandNode(command2));
								commandNode.getChildren().add(parameterNode);
							}
						}
						break;
					default:
						break;
					}								
				}
				for(ICommand command2 : command.getCommands())
					commandNode.getChildren().add(getCommandNode(command2));
				return commandNode;
			}
		};
	}

}
