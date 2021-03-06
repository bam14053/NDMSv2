package at.skobamg.ndmsv2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.service.StartupManager;
import at.skobamg.ndmsv2.view.HauptfensterController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		Application.launch(Main.class);
	}
	
	@Override
	public void start(Stage stage) throws Exception {	
		//Get all the bean components
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainAppFactory.class);
		HauptfensterController hauptfensterController = context.getBean(HauptfensterController.class);
		IEventMediator mediator = context.getBean(IEventMediator.class);
		//Load all the templates into the program		
		new StartupManager();
		mediator.loadTemplates();		
		//Close and get required instances
		context.close();
		//Start the window
		mediator.setStage(stage);
		
		Scene scene = new Scene(hauptfensterController.getView(), 1000, 700);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setTitle("NDMS v2"); // Title of program
		stage.show(); // Show the Mainwindow
	}

}
