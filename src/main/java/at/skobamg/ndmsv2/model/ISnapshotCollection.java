package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.util.ArrayList;

public interface ISnapshotCollection {
	public ArrayList<ISnapshot> getAllSnapshots();
	public ArrayList<ISnapshot> getSnapshots(String switchName, String switchVersion);
}
