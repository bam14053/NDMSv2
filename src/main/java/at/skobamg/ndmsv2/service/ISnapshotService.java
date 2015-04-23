package at.skobamg.ndmsv2.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import at.skobamg.ndmsv2.model.ISnapshot;

public interface ISnapshotService {
	public ArrayList<ISnapshot> loadSnapshots() throws ClassNotFoundException, IOException;
	public void saveSnapshot(ISnapshot snapshot) throws FileNotFoundException, IOException;
	public void saveSnapshots(ArrayList<ISnapshot> snapshots) throws FileNotFoundException, IOException;
}
