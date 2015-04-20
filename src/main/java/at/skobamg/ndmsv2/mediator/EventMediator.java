package at.skobamg.ndmsv2.mediator;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import at.skobamg.ndmsv2.logic.GenerateCommandHierachyCommand;
import at.skobamg.ndmsv2.logic.ITabsController;
import at.skobamg.ndmsv2.logic.LoadTemplatesCommand;
import at.skobamg.ndmsv2.logic.ParseHierachyCommand;
import at.skobamg.ndmsv2.logic.StartSingleConnectionCommand;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITab;
import at.skobamg.ndmsv2.model.ITemplateCollection;
import at.skobamg.ndmsv2.model.IXMLTemplate;
import at.skobamg.ndmsv2.view.HauptfensterController;
import at.skobamg.ndmsv2.view.SingleAuthenticationWindowController;
import at.skobamg.ndmsv2.view.Windows;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EventMediator implements IEventMediator {
	@Autowired
	private HauptfensterController hauptfensterController;
	@Autowired
	private ITemplateCollection templateCollection;
	@Autowired
	private ITabsController tabsContoller;	
	private Stage stage;
	private Stage tempStage;	
	
	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void displayMessage(String message) {
		System.out.println(message);
	}
	
	@Override
	public void loadTemplates() {
		LoadTemplatesCommand loadTemplatesCommand = new LoadTemplatesCommand();
		loadTemplatesCommand.setOnSucceeded(new TemplateLoadedHandler());
		loadTemplatesCommand.setOnFailed((event)->displayMessage("Fatal Error! An error occured while trying to load templates into the program"));
		loadTemplatesCommand.start();
	}

	@Override
	public void newConnection() {
		startSingleConnection("192.168.2.2", "test", "test", "test");
//		tempStage = new Stage();
//		tempStage.initOwner(stage);
//		tempStage.setScene(new Scene(Windows.loadWindow(Windows.AddTab, new AddTabWindowController(this))));
//		tempStage.setResizable(false);
//		tempStage.sizeToScene();		
//		tempStage.show();
	}

	@Override
	public void openSettings() {
//		Windows.loadWindow(Windows.SettingsWindow, settingsWindowController);
//		tempStage = new Stage();
//		tempStage.setScene(new Scene(addTabWindowController.getView()));
//		tempStage.setResizable(false);
//		tempStage.sizeToScene();		
//		tempStage.show();
	}

	@Override
	public void exit() {
		stage.close();
	}

	@Override
	public void openSingleAuthenticationWindow(String IPAddress) {
		tempStage.close();
		tempStage = new Stage();
		tempStage.initOwner(stage);
		tempStage.setScene(new Scene(Windows.loadWindow(Windows.SingleAuthenticationWindow, new SingleAuthenticationWindowController(this, IPAddress))));
		tempStage.setResizable(false);
		tempStage.sizeToScene();
		tempStage.showAndWait();
	}

	@Override
	public void startSingleConnection(String host, String username, String password, String secret) {
//		tempStage.close();		
		StartSingleConnectionCommand startSingleConnectionCommand = new StartSingleConnectionCommand(templateCollection, host, username, password, secret);
		
		//Create Progress Bar for task
		ProgressBar progressBar = new ProgressBar();
		progressBar.setProgress(0);
		progressBar.progressProperty().bind(startSingleConnectionCommand.progressProperty());
		progressBar.setPrefWidth(300);
		
		Label label = new Label();
		label.setPrefWidth(300);
		label.setWrapText(true);
		label.textProperty().bind(startSingleConnectionCommand.messageProperty());		
		
		VBox vBox = new VBox(progressBar, label);
		tempStage = new Stage();
		tempStage.initOwner(stage);
		tempStage.initStyle(StageStyle.UTILITY);
		tempStage.initModality(Modality.WINDOW_MODAL);
		tempStage.setScene(new Scene(vBox, 300, 50));
		tempStage.setTitle("Connecting to host "+host);
		tempStage.sizeToScene();
		tempStage.resizableProperty().setValue(Boolean.FALSE);
		tempStage.setOnCloseRequest((event)->startSingleConnectionCommand.cancel());	
		tempStage.show();
		
		startSingleConnectionCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {			
			@SuppressWarnings("unchecked")
			@Override
			public void handle(WorkerStateEvent arg0) {
				tempStage.close();
				ITab tab = (ITab) arg0.getSource().getValue();
				
				tabsContoller.addTab(tab);							
				addTab(tab.getTabName(), tab.getInterfaces());
				updateTemplateList(tab.getTemplates());
				GenerateCommandHierachyCommand generateCommandHierachyCommand = new GenerateCommandHierachyCommand(tab.getTemplate(), tab.getTabData());
				generateCommandHierachyCommand.setOnSucceeded((event)->hauptfensterController.setCommandHierachy((TreeItem<Node>) event.getSource().getValue()));
				generateCommandHierachyCommand.start();
			}
		});
		startSingleConnectionCommand.setOnFailed(new EventHandler<WorkerStateEvent>() {			
			@Override
			public void handle(WorkerStateEvent arg0) {
				tempStage.close();
				displayMessage(arg0.getSource().getException().getMessage());
			}
		});
		startSingleConnectionCommand.start();		
	}

	@Override
	public Stage getStage() {
		return stage;
	}
	
	class TemplateLoadedHandler implements EventHandler<WorkerStateEvent>{

		@Override
		@SuppressWarnings("unchecked")
		public void handle(WorkerStateEvent arg0) {
			templateCollection.setTemplateList((ArrayList<IXMLTemplate>)arg0.getSource().getValue());
			hauptfensterController.updateTemplateList(templateCollection.getAllTemplateNames());
		}		
	}

	@Override
	public void newMessage(String message) {
		Platform.runLater(() -> {
			hauptfensterController.newMessageLine(message);
		});
	}

	@Override
	public void updateTemplateList(ArrayList<String> templates) {
		hauptfensterController.updateTemplateList(templates);	
	}

	@Override
	public void addTab(String tabName, ArrayList<IInterface> interfaces) {	
		
		//Bind the tab interfaces to the Buttons;
		ArrayList<Button> ports = new ArrayList<Button>();
		for(IInterface inter : interfaces) {
			Button port = new Button(inter.getPortnameShort());
			port.styleProperty().bind(inter.portstatusProperty());
			port.setOnAction(hauptfensterController);
			port.setUserData(inter);
			Tooltip tooltip = new Tooltip();
			tooltip.textProperty().bind(inter.tooltipTextProperty());
			port.setTooltip(tooltip);
			ports.add(port);
		}	
		hauptfensterController.addNewTab(tabName, ports);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInterfaceCommandHierachy(IInterface interf, String tabName) {
		GenerateCommandHierachyCommand generateCommandHierachyCommand = new GenerateCommandHierachyCommand(interf, tabsContoller.getTabByName(tabName).getInterfaceSnippet());
		generateCommandHierachyCommand.setOnSucceeded((event)->hauptfensterController.setCommandHierachy((TreeItem<Node>) event.getSource().getValue()));
		generateCommandHierachyCommand.start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void generateCommandHierachy(String tabName) {
		GenerateCommandHierachyCommand generateCommandHierachyCommand = new GenerateCommandHierachyCommand(tabsContoller.getTabByName(tabName).getTemplate(), tabsContoller.getTabByName(tabName).getTabData());
		generateCommandHierachyCommand.setOnSucceeded((event)->hauptfensterController.setCommandHierachy((TreeItem<Node>) event.getSource().getValue()));
		generateCommandHierachyCommand.start();
	}

	@Override
	public void saveChangestoSwitch(String tabName, TreeItem<Node> hierachy) {
		ParseHierachyCommand saveChangesToInterfaceCommand = new ParseHierachyCommand(hierachy);
		saveChangesToInterfaceCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {			
			@Override
			public void handle(WorkerStateEvent event) {
				Stage stage = new Stage();
				
				Button yes = new Button("Schicken");
				Button no = new Button("Abbrechen");
				no.setAlignment(Pos.BASELINE_LEFT);
				yes.setAlignment(Pos.BASELINE_RIGHT);
				yes.setDefaultButton(true);
				no.setCancelButton(true);
				no.setOnAction((event2)-> stage.close());
				yes.setOnAction((event2)-> tabsContoller.getTabByName(tabName).sendCommandsToSwitch((String) event.getSource().getValue()));
				
				TextArea textArea = new TextArea((String) event.getSource().getValue());
				textArea.setEditable(false);				
				
				stage.initOwner(EventMediator.this.stage);
				stage.setScene(new Scene(new BorderPane(textArea, new Text("Die Folgende Befehle werden an den Switch geschickt.\n Wollen Sie fortfahren?"), null, new FlowPane(yes, no), null)));
				stage.setResizable(false);
				stage.sizeToScene();		
				stage.show();			
			}
		});
		saveChangesToInterfaceCommand.start();		
	}

	@Override
	public String getConsoleTextFromTab(String tabName) {
		return tabsContoller.getTabByName(tabName).getConsoleText();
	}
}
