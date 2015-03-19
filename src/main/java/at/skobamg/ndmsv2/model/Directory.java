package at.skobamg.ndmsv2.model;
import java.io.File;
import java.net.URL;

import at.skobamg.ndmsv2.view.Windows;

public class Directory {
	public static String home_directory = System.getProperty("user.dir");
	public static String vorlagen_directory = home_directory+"/Vorlagen/";
	
	public static void setVorlagenDirectory(String directory) {
		vorlagen_directory = directory;
	}
	
	public static void verzeichnisseErstellen(){
		if(!new File(vorlagen_directory).exists())
			new File(vorlagen_directory).mkdir();
	}	
}
