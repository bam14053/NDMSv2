package at.skobamg.ndmsv2.service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import at.skobamg.ndmsv2.model.Directory;

public class StartupManager {
	public StartupManager() {
		if(!new File(Directory.settings_file).exists())
			createNewSettingsFile();
		else
			loadSettingsFile();
	}
	
	private void loadSettingsFile() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(Directory.settings_file));
			Directory.setVorlagenDirectory(bufferedReader.readLine());
			Directory.setSnapshotDirectory(bufferedReader.readLine());
			bufferedReader.close();
		} catch (IOException e) {
			
		}
	}

	private void createNewSettingsFile() {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Directory.settings_file, false));
			createDirectories();
			bufferedWriter.write(Directory.vorlagen_directory);
			bufferedWriter.write(Directory.snapshots_directory);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
		}
	}
	
	private void createDirectories() {
		new File(Directory.vorlagen_directory).mkdir();
		new File(Directory.snapshots_directory).mkdir();
		new File(Directory.temp_directory).mkdir();
	}
}
