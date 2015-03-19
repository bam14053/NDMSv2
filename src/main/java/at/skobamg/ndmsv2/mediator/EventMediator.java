package at.skobamg.ndmsv2.mediator;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.ndmsv2.logic.LoadTemplatesCommand;
import at.skobamg.ndmsv2.model.ITemplateCollection;
import at.skobamg.ndmsv2.model.IXMLTemplate;
import at.skobamg.ndmsv2.view.AddTabWindowController;
import at.skobamg.ndmsv2.view.HauptfensterController;
import at.skobamg.ndmsv2.view.Windows;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EventMediator implements IEventMediator {
	@Autowired
	private HauptfensterController hauptfensterController;
	@Autowired
	private ITemplateCollection templateCollection;
	private AddTabWindowController addTabWindowController = new AddTabWindowController();
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
	
	class TemplateLoadedHandler implements EventHandler<WorkerStateEvent>{

		@SuppressWarnings("unchecked")
		public void handle(WorkerStateEvent arg0) {
			templateCollection.setTemplateList((ArrayList<IXMLTemplate>)arg0.getSource().getValue());
			hauptfensterController.updateTemplateList(templateCollection.getAllTemplates());
		}
		
	}

	public void newConnection() {
		Windows.loadWindow(Windows.AddTab, addTabWindowController);
		tempStage = new Stage();
		tempStage.setScene(new Scene(addTabWindowController.getView()));
		tempStage.setResizable(false);
		tempStage.sizeToScene();		
		tempStage.show();
	}

	public void openSettings() {
		Windows.loadWindow(Windows.SettingsWindow, );
	}

}
