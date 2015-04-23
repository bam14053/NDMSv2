package at.skobamg.ndmsv2.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import at.skobamg.ndmsv2.model.Directory;
import at.skobamg.ndmsv2.model.ISnapshot;

public class SnapshotService implements ISnapshotService {

	@Override
	public ArrayList<ISnapshot> loadSnapshots() throws ClassNotFoundException, IOException {
		ArrayList<ISnapshot> snapshots = new ArrayList<ISnapshot>();
		for(File file : new File(Directory.snapshots_directory).listFiles()) {
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
			snapshots.add((ISnapshot) objectInputStream.readObject());
			objectInputStream.close();
		}
		return snapshots;
	}

	@Override
	public void saveSnapshot(ISnapshot snapshot) throws FileNotFoundException, IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Directory.snapshots_directory+"/"+snapshot.getName()+".snp"));
		objectOutputStream.writeObject(snapshot);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	@Override
	public void saveSnapshots(ArrayList<ISnapshot> snapshots) throws FileNotFoundException, IOException {
		for(ISnapshot snapshot : snapshots) {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Directory.snapshots_directory+"/"+snapshot.getName()+".snp"));
			objectOutputStream.writeObject(snapshot);
			objectOutputStream.flush();
			objectOutputStream.close();
		}
	}

}
