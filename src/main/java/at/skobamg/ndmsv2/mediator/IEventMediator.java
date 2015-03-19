package at.skobamg.ndmsv2.mediator;
import javafx.stage.Stage;

public interface IEventMediator {
	public void setStage(Stage stage);
	public void loadTemplates();
	public void newConnection();
	public void openSettings();
}
