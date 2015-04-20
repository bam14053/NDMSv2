/**
 * 
 */
package at.skobamg.ndmsv2.logic;
import at.skobamg.generator.model.ICommand;
import at.skobamg.generator.model.IParameter;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

/**
 * @author abideen
 *
 */
public class ParseHierachyCommand extends Service<String> {
	private TreeItem<Node> hierachy;
	
	public ParseHierachyCommand(TreeItem<Node> hierachy) {
		this.hierachy = hierachy;
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<String> createTask() {
		// TODO Auto-generated method stub
		return new Task<String>() {
			
			@Override
			protected String call() throws Exception {
				String command = "";
				TreeItem<Node> snippetItem = hierachy.getChildren().get(0);
				for(TreeItem<Node> sectionItem : snippetItem.getChildren()) {
					for(TreeItem<Node> commandItem : sectionItem.getChildren()) {
						command += parseCommand(commandItem);
					}
				}
				return command;
			}			
			
			protected String parseParameter(IParameter parameter, HBox hBox) {
				String parameters = "";
				for(int i = 0; i < parameter.getParameters().size(); i++) {
					parameters += ((TextField) hBox.getChildren().get((i+1))).getText()+" ";
				}
				return parameters;
			}
			
			protected String parseCommand(TreeItem<Node> commandItem) {
				ICommand command = (ICommand) commandItem.getValue().getUserData();
				String commandText = "";
				switch (command.getType()) {
				case Bool:
					boolean boolInput = ((CheckBox)((HBox) commandItem.getValue()).getChildren().get(1)).isSelected();
					commandText += (boolInput)? command.getExeccommand()+"\n" :"no "+command.getExeccommand()+"\n";
					break;
				case Choice:
					for(int i = 0; i < command.getParameters().size(); i++) {
						if(command.getParameters().get(i).getCommands().isEmpty()) {
							HBox hBox = (HBox) commandItem.getChildren().get(i).getValue();
							if(((RadioButton)hBox.getChildren().get(0)).isSelected()){
								commandText += command.getExeccommand()+ " ";
								if(!command.getParameters().get(i).getExeccommand().equals("null"))
										commandText += command.getParameters().get(i).getExeccommand()+" ";								
								commandText += parseParameter(command.getParameters().get(i), hBox)+"\n";
							}
						}else {
							TreeItem<Node> parameterItem = commandItem.getChildren().get(i);
							HBox hBox = (HBox) parameterItem.getValue();
							if(((RadioButton)hBox.getChildren().get(0)).isSelected()) {
								commandText += command.getExeccommand()+ " "+command.getParameters().get(i).getExeccommand()+" "+parseParameter((IParameter) parameterItem.getValue().getUserData(), hBox)+"\n";
								for(int i2 = 0; i2 < parameterItem.getChildren().size(); i2++) 
									commandText += parseCommand(parameterItem.getChildren().get(i2));
							}
						}
					}		
					break;
				case Multi:
					for(int i = 0; i < command.getParameters().size(); i++) {
						if(command.getParameters().get(i).getCommands().isEmpty()) {
							HBox hBox = (HBox) commandItem.getChildren().get(i).getValue();
							if(((CheckBox)hBox.getChildren().get(0)).isSelected()){
								commandText += command.getExeccommand()+ " "+ 
										command.getParameters().get(i).getExeccommand()+" "+
											parseParameter(command.getParameters().get(i), hBox)+"\n";
							}
						}else {
							TreeItem<Node> parameterItem = commandItem.getChildren().get(i);
							HBox hBox = (HBox) parameterItem.getValue();
							if(((CheckBox)hBox.getChildren().get(0)).isSelected()) {
								commandText += command.getExeccommand()+ " "+command.getParameters().get(i).getExeccommand()+" "+parseParameter((IParameter) parameterItem.getValue().getUserData(), hBox)+"\n";
								for(int i2 = 0; i2 < parameterItem.getChildren().size(); i2++) 
									commandText += parseCommand(parameterItem.getChildren().get(i2));
							}
						}
					}	
					break;
				case Delim:
				case Integer:
				case String:					
					String textInput = ((TextField)((HBox) commandItem.getValue()).getChildren().get(1)).getText();
					if(!textInput.isEmpty())
						commandText += command.getExeccommand() +" "+textInput+"\n";
					break;
				default:
					break;
				}
				for(int i = 0; i < command.getCommands().size(); i++)
					commandText += parseCommand(commandItem.getChildren().get(i));
				return commandText;
			}
		};
	}
}
