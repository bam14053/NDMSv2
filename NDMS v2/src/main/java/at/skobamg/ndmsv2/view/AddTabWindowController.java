package at.skobamg.ndmsv2.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AddTabWindowController implements IController{
	@FXML
	private VBox view;
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
	
	public Pane getView() {
		return view;
	}

}
