package at.skobamg.ndmsv2.model;

public class Directory {
	public static String home_directory = System.getProperty("user.dir");
	public static final String settings_file = home_directory+"/ndms.conf";
	public static String vorlagen_directory = home_directory+"/Vorlagen/";
	public static String snapshots_directory = home_directory+"/Snapshots/";
	public static final String temp_directory = home_directory+"/temp/";
	
	public static void setVorlagenDirectory(String directory) {
		vorlagen_directory = directory;
	}
	
	public static void setSnapshotDirectory(String directory) {
		snapshots_directory = directory;
	}	
}
