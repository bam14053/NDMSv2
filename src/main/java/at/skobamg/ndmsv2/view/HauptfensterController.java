package at.skobamg.ndmsv2.view;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.ndmsv2.logic.LoadTemplatesCommand;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class HauptfensterController implements IController{
	@Autowired
	private IEventMediator mediator;
	@FXML
	private VBox view;
	@FXML
	private ComboBox<String> templateList;
	
	public Pane getView() {
		return view;
	}
	
	public void newConnection() {
		mediator.newConnection();
	}
	
	public void open() {
		//Gespeicherte Session öffnen
	}
	
	public void save() {
		//Session Speichern
	}
	
	public void saveAs() {
		//Session speichern unter
	}
	
	public void openSettings() {
		//In den einstellungen gehen
		mediator.openSettings();
	}

	public void updateTemplateList(ArrayList<String> allTemplates) {
		templateList.getItems().clear();
		for(String templateName : allTemplates)
			templateList.getItems().add(templateName);
	}
}
