package at.skobamg.ndmsv2.view;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public enum Windows {
	MainWindow, AddTab, SettingsWindow, SingleAuthenticationWindow, SnapshotWindow;
	
	public static IController controllerLaden(Windows window) {		
		FXMLLoader loader = new FXMLLoader(getWindow(window));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loader.getController();
	}
	
	public static Parent loadWindow(Windows window, IController controller) {
		FXMLLoader loader = new FXMLLoader(getWindow(window));
		try {
			loader.setController(controller);
			return (Parent)loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		case SettingsWindow:
			windowFileName = "SettingWindow.fxml";
			break;
		case SingleAuthenticationWindow:
			windowFileName = "SingleAuthenticationWindow.fxml";
			break;
		case SnapshotWindow:
			windowFileName = "SnapshotWindow.fxml";
		}
		return Thread.currentThread().getContextClassLoader().getResource(windowFileName);
	}
}
