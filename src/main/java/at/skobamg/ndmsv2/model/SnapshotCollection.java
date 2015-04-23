package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.util.ArrayList;

import at.skobamg.ndmsv2.service.SnapshotService;

public class SnapshotCollection implements ISnapshotCollection {
	private ArrayList<ISnapshot> snapshots = new ArrayList<ISnapshot>();
	
	public SnapshotCollection(){
		try {
			snapshots = new SnapshotService().loadSnapshots();
		} catch (ClassNotFoundException | IOException e) {
		}
	}
	
	@Override
	public ArrayList<ISnapshot> getAllSnapshots(){
		return snapshots;
	}

	@Override
	public ArrayList<ISnapshot> getSnapshots(String switchName,
			String switchVersion) {
		ArrayList<ISnapshot> snapshots = new ArrayList<ISnapshot>(this.snapshots);
		snapshots.removeIf((snapshot)-> !snapshot.getSwitchName().equals(switchName)&&!snapshot.getSwitchVersion().equals(switchName));
		return snapshots;
	}

}
