/**
 * 
 */
package at.skobamg.ndmsv2.view;
import sun.security.util.Password;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author abideen
 *
 */
public class SingleAuthenticationWindowController implements IController {
	@FXML
	private PasswordField passwort;
    @FXML
    private PasswordField secret;
    @FXML
    private TextField username;
	private IEventMediator mediator;
	private String ipAddress;
	
	public SingleAuthenticationWindowController(IEventMediator mediator, String ipAddress) {
		this.mediator = mediator;
		this.ipAddress = ipAddress;
	}
	
	@FXML
    public void tasteGedrueckt(KeyEvent keyEvent) {
		if(keyEvent.getCode().equals(KeyCode.TAB)) {
			if(passwort.isFocused()) secret.requestFocus();
			else if (secret.isFocused()) username.requestFocus();
		}
		if(keyEvent.getCode().equals(KeyCode.ENTER)) {
			if(passwort.getText().isEmpty() || secret.getText().isEmpty() || username.getText().isEmpty()){
				displayConfirmation();
			}
		}
		
    }
	
	@FXML
	public void weiter() {
		if(passwort.getText().isEmpty() || secret.getText().isEmpty() || username.getText().isEmpty()){
			displayConfirmation();
		}else
			mediator.startSingleConnection(ipAddress, username.getText(), passwort.getText(), secret.getText());
	}

	private void displayConfirmation() {
		final Stage stage = new Stage();
		Label label = new Label("Sie haben nicht alle Daten eingegeben, sind Sie sich sicher dass sie fortfahren möchten?");
		Button yes = new Button("Ja");
		Button no = new Button("Nein");
		VBox vBox = new VBox(5);
		HBox hBox = new HBox(10);
		
		hBox.getChildren().add(yes);
		hBox.getChildren().add(no);		
		
		vBox.getChildren().add(label);	
		vBox.getChildren().add(hBox);
		VBox.setMargin(hBox, new Insets(0, 0, 0, 200));
		yes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {				
				mediator.startSingleConnection(ipAddress, username.getText(), passwort.getText(), secret.getText());
				stage.close();
			}
		});
		no.setCancelButton(true);		
		no.setOnAction(new EventHandler<ActionEvent>() {			
			public void handle(ActionEvent arg0) {
				stage.close();
			}
		});
		stage.setScene(new Scene(vBox));
		stage.sizeToScene();
		stage.show();
	}

	/* (non-Javadoc)
	 * @see at.skobamg.ndmsv2.view.IController#getView()
	 */
	public Pane getView() {
		return null;
	}

}
