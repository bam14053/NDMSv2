package at.skobamg.ndmsv2.view;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

public class AddTabWindowController implements IController{
	@FXML
	private ToggleGroup radiotoggle;
	@FXML
	private RadioButton rbaddress;
	@FXML
	private RadioButton rbrange;
	@FXML
	private RadioButton rboff;
	@FXML
	private RestrictiveTextField ipaddress1;
	@FXML
	private RestrictiveTextField ipaddress2;
	@FXML
	private RestrictiveTextField ipaddress3;
	@FXML
	private RestrictiveTextField ipaddress4;
	@FXML
	private RestrictiveTextField iprange1;
	@FXML
	private RestrictiveTextField iprange2;
	@FXML
	private RestrictiveTextField iprange3;
	@FXML
	private RestrictiveTextField iprange4;
	@FXML
	private RestrictiveTextField iprange5;
	@FXML
	private RestrictiveTextField iprange6;
	@FXML
	private RestrictiveTextField iprange7;
	@FXML
	private RestrictiveTextField iprange8;
	@FXML
	private TextField offline1;
	@FXML
	private Label errorlabel;
	@FXML
	private Label vonlabel;
	@FXML
	private Label bislabel;
	@FXML
	private Label namelabel;
	@FXML
	private Label portanzlabel;
	@FXML
	private Label versionlabel;
	@FXML
	private Label typelabel;
	@FXML
	private TextField portanz;
	@FXML
	private TextField version;
	@FXML
	private TextField type;
	private IEventMediator mediator;
	
	public AddTabWindowController(IEventMediator mediator) {
		this.mediator = mediator;
	}
	
	public Pane getView() {
		return null;
	}
	
	private void disableAllInput() {
		ipaddress1.setDisable(true);
		ipaddress2.setDisable(true);
		ipaddress3.setDisable(true);
		ipaddress4.setDisable(true);
		ipaddress1.setText("");
		ipaddress2.setText("");
		ipaddress3.setText("");
		ipaddress4.setText("");
		
		iprange1.setText("");
		iprange2.setText("");
		iprange3.setText("");
		iprange4.setText("");
		iprange5.setText("");
		iprange6.setText("");
		iprange7.setText("");
		iprange8.setText("");
		
		iprange1.setDisable(true);
		iprange2.setDisable(true);
		iprange3.setDisable(true);
		iprange4.setDisable(true);
		iprange5.setDisable(true);
		iprange6.setDisable(true);
		iprange7.setDisable(true);
		iprange8.setDisable(true);		

		offline1.setText("");
		portanz.setText("");
		offline1.setDisable(true);
		portanz.setDisable(true);
		
		vonlabel.setDisable(true);
		bislabel.setDisable(true);
		
		namelabel.setDisable(true);
		portanzlabel.setDisable(true);
		
		typelabel.setDisable(true);
		versionlabel.setDisable(true);
		type.setDisable(true);
		version.setDisable(true);
		type.setText("");
		version.setText("");
		
		ipaddress1.setDisable(true);
		ipaddress2.setDisable(true);
		ipaddress3.setDisable(true);
		ipaddress4.setDisable(true);
	}
	
	
	public void iptoggle(){	
		disableAllInput();
		ipaddress1.setDisable(false);
		ipaddress2.setDisable(false);
		ipaddress3.setDisable(false);
		ipaddress4.setDisable(false);
	}

	
	public void rangetoggle(){
		disableAllInput();
		iprange1.setDisable(false);
		iprange2.setDisable(false);
		iprange3.setDisable(false);
		iprange4.setDisable(false);
		iprange5.setDisable(false);
		iprange6.setDisable(false);
		iprange7.setDisable(false);
		iprange8.setDisable(false);
		vonlabel.setDisable(false);
		bislabel.setDisable(false);
	}
	
	public void startconnection() {
		if(radiotoggle.getSelectedToggle().equals(rbaddress)) {
			if(validateIPAddress(ipaddress1.getText()) && validateIPAddress(ipaddress2.getText()) 
					&& validateIPAddress(ipaddress3.getText()) && validateIPAddress(ipaddress4.getText())) {
				mediator.openSingleAuthenticationWindow(extractIPAddress(ipaddress1, ipaddress2, ipaddress3, ipaddress4));
			}
		}else if(radiotoggle.getSelectedToggle().equals(rbrange)) {
			if(validateIPAddress(iprange1.getText()) && validateIPAddress(iprange2.getText()) && validateIPAddress(iprange3.getText()) 
					&& validateIPAddress(iprange4.getText()) && validateIPAddress(iprange5.getText()) && validateIPAddress(iprange6.getText())
					&& validateIPAddress(iprange7.getText()) && validateIPAddress(iprange8.getText()))
				;
		}else if(radiotoggle.getSelectedToggle().equals(rboff)) {
			;
		}
			
	}

	private String extractIPAddress(RestrictiveTextField ocatate1, RestrictiveTextField ocatate2, RestrictiveTextField ocatate3, RestrictiveTextField ocatate4) {
		return ocatate1.getText()+"."+ocatate2.getText()+"."+ocatate3.getText()+"."+ocatate4.getText();
	}
	
	private boolean validateIPAddress(String address) {
		if(address.isEmpty() || Integer.parseInt(address) > 255)
			return false;
		return true;
	}
	
	public void offtoggle()
	{
		disableAllInput();
		
		namelabel.setDisable(false);
		portanzlabel.setDisable(false);
		
		offline1.setDisable(false);
		portanz.setDisable(false);
		
		typelabel.setDisable(false);
		versionlabel.setDisable(false);
		type.setDisable(false);
		version.setDisable(false);
	}

}
