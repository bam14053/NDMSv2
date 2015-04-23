package at.skobamg.ndmsv2.view;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.IInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class HauptfensterController implements IController, EventHandler<ActionEvent>{
	@Autowired
	private IEventMediator mediator;
	@FXML
	private SplitPane view;
	private TreeView<Node> tree = new TreeView<Node>();
	private BorderPane view2;
	@FXML
	private ComboBox<String> templateList;
	@FXML
	private TabPane tabpane;
	private Stage stage = new Stage();
	private TextFlow consoleText = new TextFlow(new Text());
	ScrollPane consoleScroll = new ScrollPane(consoleText);
	
	@Override
	public SplitPane getView() {				
		//Initialize the console window	    
		consoleScroll.setStyle("-fx-background-color: #000000;");
		consoleScroll.setFitToHeight(true);
		consoleScroll.setFitToWidth(true);	
		
		Button button = new Button("Änderungen einspielen");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mediator.saveChangestoSwitch(tabpane.getSelectionModel().getSelectedItem().getText(), tree.getRoot());
			}
		});
		
		FlowPane flowPane = new FlowPane(button);
		flowPane.setAlignment(Pos.BASELINE_RIGHT);		
		view2 = new BorderPane(tree, null, null, flowPane, null);
		
		view.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				view2.setPrefHeight(newValue.doubleValue());
			}
		});
		
		consoleText.setStyle("-fx-background-color: #000000;");
		consoleText.setLineSpacing(0);
		
		tabpane.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<Tab>>() {
			@Override
			public void changed(
					ObservableValue<? extends SingleSelectionModel<Tab>> observable, 
							SingleSelectionModel<Tab> oldValue, SingleSelectionModel<Tab> newValue) {
				mediator.generateCommandHierachy(newValue.getSelectedItem().getText());
				consoleText.getChildren().clear();
				String message = mediator.getConsoleTextFromTab(newValue.getSelectedItem().getText());
				for(String line : message.split("\n"))
					if(line.endsWith(newValue.getSelectedItem().getText()+">") || line.endsWith(newValue.getSelectedItem().getText()+"#"))
						newCommandLine(line);
					else
						newMessageLine(line);				
				mediator.listenToTab(newValue.getSelectedItem().getText());
			}
		});
		tabpane.getTabs().addListener(new ListChangeListener<Tab>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Tab> c) {
				if(tabpane.getTabs().isEmpty()) view.getItems().remove(view2);
			}
		});
		stage.setScene(new Scene(consoleScroll, 300, 200));
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
	
	public void setCommandHierachy(TreeItem<Node> hierachy) {
		tree.setRoot(hierachy);
	}
	
	public void openSnapshotManager() {
		if(tabpane.getTabs().size() > 0)
			mediator.openSnapshotManager(tabpane.getSelectionModel().getSelectedItem().getText());
		else
			mediator.openSnapshotManager();
	}
	
	public void addNewTab(String tabName, ArrayList<Button> ports) {
		if(tabpane.getTabs().size() == 0)
			view.getItems().add(view2);
		//Creating required controls		
		Tab tab = new Tab(tabName);	
		Button refresh = new Button("Befehlshierachie Neu Laden");
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mediator.generateCommandHierachy(tabName);
			}
		});
		FlowPane flowPane = new FlowPane(refresh);
		flowPane.setAlignment(Pos.BASELINE_RIGHT);
		GridPane gridPane = new GridPane();	
		gridPane.setAlignment(Pos.CENTER);
		BorderPane borderPane = new BorderPane(gridPane, null, null, flowPane, null);
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
		
		tab.setContent(borderPane);
		tabpane.getTabs().add(tab);
		tabpane.getSelectionModel().select(tab);	
		mediator.listenToTab(tabName);
	}
	
	public void openConsole(ActionEvent actionEvent) {
		if(stage.isShowing()) mediator.displayMessage("The Console window is already open");
		else stage.show();
	}
	
	public void open() {
		//Gespeicherte Session öffnen
	}
	
	public void closeConnection() {
		if(tabpane.getTabs().size() > 0)
			tabpane.getTabs().remove(tabpane.getSelectionModel().getSelectedItem());
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
		templateList.getSelectionModel().select(0);
	}
	
	public void newCommandLine(String commandLine) {
		Text text = new Text(commandLine);
		text.setFill(Paint.valueOf(Color.LIGHTBLUE.toString()));
		consoleText.getChildren().add(text);
	}
	
	public void newMessageLine(String messageLine) {
		Text text = new Text(messageLine);
		text.setFill(Paint.valueOf(Color.BEIGE.toString()));
		consoleText.getChildren().add(text);
	}
	
	@Override
	public void handle(ActionEvent event) {
		IInterface selectedInterface = (IInterface) ((Button)event.getSource()).getUserData();		
		mediator.setInterfaceCommandHierachy(selectedInterface, tabpane.getSelectionModel().getSelectedItem().getText());
		
		Stage stage = new Stage();
		TextArea textArea = new TextArea(selectedInterface.getRunningConfig());
		textArea.setEditable(false);
		stage.setScene(new Scene(textArea, 300, 200));
		stage.setTitle("Running config for port "+selectedInterface.getPortnameShort());
		stage.initOwner(mediator.getStage());
		stage.setAlwaysOnTop(true);
		stage.show();
	}
		
}







