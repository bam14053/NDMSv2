package at.skobamg.ndmsv2.mediator;

import java.util.ArrayList;
import at.skobamg.ndmsv2.model.IInterface;
import javafx.stage.Stage;

public interface IEventMediator {
	public void setStage(Stage stage);
	public Stage getStage();
	public void loadTemplates();
	public void updateTemplateList(ArrayList<String> templates);
	public void newConnection();
	public void openSettings();
	public void exit();
	public void displayMessage(String message);
	public void startSingleConnection(String ipAddress, String username, String password, String secret);
	public void openSingleAuthenticationWindow(String IPAddress);
	public void newCommandLine(String commandLine);
	public void newMessageLine(String messageLine);
	public void addTab(String tabName, ArrayList<IInterface> interfaces);
}
