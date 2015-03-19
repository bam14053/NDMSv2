package at.skobamg.ndmsv2.view;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;

public enum Windows {
	MainWindow, AddTab;
	
	public static IController controllerLaden(Windows window) {		
		FXMLLoader loader = new FXMLLoader(getWindow(window));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loader.getController();
	}
	
	public static void loadWindow(Windows window, IController controller) {
		FXMLLoader loader = new FXMLLoader(getWindow(window));
		try {
			loader.setController(controller);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static URL getWindow(Windows window) {
		String windowFileName = null;
		switch (window) {		
		case AddTab:
			windowFileName = "AddTab.fxml";
			break;
		case MainWindow:
			windowFileName = "Hauptfenster.fxml";
			break;
		}
		return Thread.currentThread().getContextClassLoader().getResource(windowFileName);
	}
}
