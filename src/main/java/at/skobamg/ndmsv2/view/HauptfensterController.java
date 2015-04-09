package at.skobamg.ndmsv2.view;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.IInterface;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class HauptfensterController implements IController{
	@Autowired
	private IEventMediator mediator;
	@FXML
	private VBox view;
	@FXML
	private ComboBox<String> templateList;
	@FXML
	private TabPane tabpane;
	private Stage stage = new Stage();
	private TextFlow consoleText = new TextFlow(new Text());
	ScrollPane scrollPane = new ScrollPane(consoleText);
	
	public Pane getView() {		
		//Initialize the console window	    
		scrollPane.setStyle("-fx-background-color: #000000;");
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);		
		
		consoleText.setStyle("-fx-background-color: #000000;");
		consoleText.setLineSpacing(0);
	    
		stage.setScene(new Scene(scrollPane, 300, 200));
		stage.setTitle("Console Window");
		stage.initOwner(mediator.getStage());
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);
		//Return the view
		return view;
	}
	
	public void newConnection() {
		mediator.newConnection();
	}
	
	public void addNewTab(String tabName, ArrayList<Button> ports) {
		Tab tab = new Tab(tabName);
		GridPane gridPane = new GridPane();
		int row = 0;
		int col = 0;
		for(Button port : ports) {
			GridPane.setMargin(port, new Insets(5, 5, 5, 5));
			gridPane.add(port, row, col);				
			if(row == 11 && col == 1) {									
				row = 0;
				col = 2;
				continue;
			}				
			if(col%2==0) col++;
			else {
				col--;
				row++;
			}
		}
		tab.setContent(gridPane);
		tabpane.getTabs().add(tab);
		tabpane.getSelectionModel().select(tab);
	}
	
	public void openConsole(ActionEvent actionEvent) {
		if(stage.isShowing()) mediator.displayMessage("The Console window is already open");
		else stage.show();
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

	public void exit() {
		mediator.exit();
	}

	public void updateTemplateList(ArrayList<String> allTemplates) {
		templateList.getItems().clear();
		for(String templateName : allTemplates)
			templateList.getItems().add(templateName);
	}
	
	public void newCommandLine(String commandLine) {
		Text text = new Text(commandLine);
		text.setFill(Paint.valueOf(Color.LIGHTBLUE.toString()));
		consoleText.getChildren().add(text);
		consoleText.layout();
		scrollPane.layout();
	}
	
	public void newMessageLine(String messageLine) {
		Text text = new Text(messageLine);
		text.setFill(Paint.valueOf(Color.BEIGE.toString()));
		consoleText.getChildren().add(text);
		consoleText.layout();
		scrollPane.layout();
	}
}
