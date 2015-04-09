package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import at.skobamg.ndmsv2.model.IInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class IntStatusExtractorCommand extends Service<Void> {
	private ArrayList<IInterface> old_ports;
	private ArrayList<IInterface> new_ports;
	
	public IntStatusExtractorCommand(
			ArrayList<IInterface> old_ports, ArrayList<IInterface> new_ports) {
		super();
		this.old_ports = old_ports;
		this.new_ports = new_ports;
	}



	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {				
				for(IInterface port : new_ports) {
					IInterface portTemp = old_ports.get(old_ports.indexOf(port));
					if(!portTemp.getIpaddress().equals(port.getIpaddress()))
						portTemp.setIpaddress(port.getIpaddress());
					if(!portTemp.getProtocolStatus().equals(port.getProtocolStatus()))
						portTemp.setProtocolStatus(port.getProtocolStatus());
					if(!portTemp.getPortstatus().equals(port.getPortstatus()))
						portTemp.setStatus(port.getStatus());
				}
				return null;
			}
		};
	}

}
