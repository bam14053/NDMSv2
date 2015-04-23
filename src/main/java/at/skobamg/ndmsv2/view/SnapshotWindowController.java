package at.skobamg.ndmsv2.view;

import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.ISnapshot;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class SnapshotWindowController implements IController{
    @FXML
    private TextArea snapshot_text;
    
    @FXML
    private TreeView<Object> snapshotList;
    
    @FXML
    private Button snapshot_button;       
    private IEventMediator mediator;     
    private String tabName;
    
    public SnapshotWindowController(IEventMediator mediator, TreeItem<Object> snapshots, String tabName) {
    	this.mediator = mediator;
    	this.tabName = tabName;    	
    	snapshot_button.setVisible(true);
    	snapshotList.setRoot(snapshots);
	}   
    
    public SnapshotWindowController(IEventMediator mediator, TreeItem<Object> snapshots) {
    	this.mediator = mediator;
    	snapshotList.setRoot(snapshots);
	}

    @FXML
    void sendSnapshot() {
    }

    @FXML
    void addSnapshot() {
    }

    @FXML
    void deleteSnapshot() {
    }

	@Override
	public Control getView() {
		return null;
	}

}

