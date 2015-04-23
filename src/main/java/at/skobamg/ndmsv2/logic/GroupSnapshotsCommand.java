package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import java.util.HashMap;

import at.skobamg.ndmsv2.model.ISnapshot;
import at.skobamg.ndmsv2.model.Snapshot;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class GroupSnapshotsCommand extends Service<TreeItem<Object>> {
	private ArrayList<String> groups = new ArrayList<String>();
	private ArrayList<ISnapshot> snapshots;

	public GroupSnapshotsCommand(ArrayList<ISnapshot> snapshots) {
		this.snapshots = snapshots;
	}

	@Override
	protected Task<TreeItem<Object>> createTask() {
		return new Task<TreeItem<Object>>() {

			@Override
			protected TreeItem<Object> call() throws Exception {
				TreeItem<Object> root = new TreeItem<Object>();
				for (int i = 0; i < snapshots.size(); i++) {
					if (groups.contains(snapshots.get(i).getSwitchName() + "-"
							+ snapshots.get(i).getSwitchVersion())) {
						groups.add(snapshots.get(i).getSwitchName() + "-"
								+ snapshots.get(i).getSwitchVersion());
						TreeItem<Object> group = new TreeItem<Object>(snapshots
								.get(i).getSwitchName()
								+ "-"
								+ snapshots.get(i).getSwitchVersion());
						for (int i2 = 0; i2 < snapshots.size(); i2++) {
							if (group.getValue().equals(
									snapshots.get(i2).getSwitchName()
											+ "-"
											+ snapshots.get(i2)
													.getSwitchVersion()))
								group.getChildren()
										.add(new TreeItem<Object>(snapshots
												.get(i2)));
						}
						root.getChildren().add(group);
					}
				}

				return root;
			}
		};
	}

}
