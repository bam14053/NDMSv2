package at.skobamg.ndmsv2.mediator;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.ndmsv2.logic.ITabsController;
import at.skobamg.ndmsv2.logic.LoadTemplatesCommand;
import at.skobamg.ndmsv2.logic.StartSingleConnectionCommand;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITemplateCollection;
import at.skobamg.ndmsv2.model.IXMLTemplate;
import at.skobamg.ndmsv2.view.HauptfensterController;
import at.skobamg.ndmsv2.view.SingleAuthenticationWindowController;
import at.skobamg.ndmsv2.view.Windows;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
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
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void displayMessage(String message) {
		System.out.println(message);
	}
	
	public void loadTemplates() {
		LoadTemplatesCommand loadTemplatesCommand = new LoadTemplatesCommand();
		loadTemplatesCommand.setOnSucceeded(new TemplateLoadedHandler());
		loadTemplatesCommand.setOnFailed(new EventHandler<WorkerStateEvent>() {
			public void handle(WorkerStateEvent arg0) {
				displayMessage("Fatal Error! An error occured while trying to load templates into the program");
			}
		});
		loadTemplatesCommand.start();
	}

	public void newConnection() {
		startSingleConnection("192.168.1.12", "test", "test", "test");
//		tempStage = new Stage();
//		tempStage.initOwner(stage);
//		tempStage.setScene(new Scene(Windows.loadWindow(Windows.AddTab, new AddTabWindowController(this))));
//		tempStage.setResizable(false);
//		tempStage.sizeToScene();		
//		tempStage.show();
	}

	public void openSettings() {
//		Windows.loadWindow(Windows.SettingsWindow, settingsWindowController);
//		tempStage = new Stage();
//		tempStage.setScene(new Scene(addTabWindowController.getView()));
//		tempStage.setResizable(false);
//		tempStage.sizeToScene();		
//		tempStage.show();
	}

	public void exit() {
		stage.close();
	}

	public void openSingleAuthenticationWindow(String IPAddress) {
		tempStage.close();
		tempStage = new Stage();
		tempStage.initOwner(stage);
		tempStage.setScene(new Scene(Windows.loadWindow(Windows.SingleAuthenticationWindow, new SingleAuthenticationWindowController(this, IPAddress))));
		tempStage.setResizable(false);
		tempStage.sizeToScene();
		tempStage.showAndWait();
	}

	public void startSingleConnection(String host, String username, String password, String secret) {
//		tempStage.close();		
		StartSingleConnectionCommand startSingleConnectionCommand = new StartSingleConnectionCommand(tabsContoller, templateCollection, host, username, password, secret);
		
		//Create Progress Bar for task
		ProgressBar progressBar = new ProgressBar();
		progressBar.setProgress(0.01);
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
		tempStage.setOnCloseRequest((event)->event.consume());		
		tempStage.show();
		
		startSingleConnectionCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {			
			@Override
			public void handle(WorkerStateEvent arg0) {
				tempStage.close();
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

	public Stage getStage() {
		return stage;
	}
	
	class TemplateLoadedHandler implements EventHandler<WorkerStateEvent>{

		@SuppressWarnings("unchecked")
		public void handle(WorkerStateEvent arg0) {
			templateCollection.setTemplateList((ArrayList<IXMLTemplate>)arg0.getSource().getValue());
			hauptfensterController.updateTemplateList(templateCollection.getAllTemplateNames());
		}		
	}
	
	@Override
	public void newCommandLine(String commandLine) {
		Platform.runLater(() -> {
			hauptfensterController.newCommandLine(commandLine);
		});
	}

	@Override
	public void newMessageLine(String messageLine) {
		Platform.runLater(() -> {
			hauptfensterController.newMessageLine(messageLine);
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
		int i = 0;
		for(IInterface inter : interfaces) {
			Button port = new Button(inter.getPortnameShort());
			port.styleProperty().bind(inter.portstatusProperty());
			port.setUserData(i);
			Platform.runLater(() -> {
				Tooltip tooltip = new Tooltip();
				tooltip.textProperty().bind(inter.tooltipTextProperty());
				port.setTooltip(tooltip);
			});	
			ports.add(port);
			i++;
		}
				
		Platform.runLater(() -> {
			hauptfensterController.addNewTab(tabName, ports);
		});
		
	}
}
