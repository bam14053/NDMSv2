package at.skobamg.ndmsv2.model;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Interface implements IInterface {	
	private String portnameLong;
	private String portnameShort;
	private String ipaddress;
	private Portstatus status, protocol;
	private StringProperty portstatus = new SimpleStringProperty("");
	private StringProperty tooltipText = new SimpleStringProperty("");
	private StringProperty runningConfig = new SimpleStringProperty("");
	private HashMap<String, String> interfaceData = new HashMap<String, String>();
	private boolean bound;
	
	//Static Variables
	public static final String HEADER_INTERFACE = "interface";
	public static final String HEADER_IPADDRESS = "ip-address";
	public static final String HEADER_STATUS = "status";
	public static final String HEADER_PROTOCOL = "protocol";
	
	public static List<String> getHeaders(){						
		return Arrays.asList(new String[] {
				HEADER_INTERFACE, HEADER_IPADDRESS, HEADER_STATUS, HEADER_PROTOCOL
			});
	}
	
	@Override
	public final String getPortstatus() { 
		return portstatus.get(); 
	}
	
	private void setPortStatus(String status) {	
		if(!isBound())
			portstatus.set(status);
		else{
			Platform.runLater(()->{
				portstatus.set(status);
			});
			generateTooltipText();
		}			
	}
	
	@Override
	public void determinePortStatus() {	
		if(protocol == null || status == null) return;
		switch (status) {		
		case up:
			switch (protocol) {
			case up:
				setPortStatus("-fx-base: #4CC417;");
				break;
			case down:
				setPortStatus("-fx-base: #FFFF00;");
				break;
			default:
				break;			
			}
			break;
		case down:
			setPortStatus("-fx-base: #954535;");
			break;
		case administratively:
			setPortStatus("-fx-base: #660000");
			break;
		}
	}
	
	@Override
	public StringProperty portstatusProperty() {
		bound = true;
		generateTooltipText();
		return portstatus;
	}
	
	@Override
	public String getPortnameLong() {
		return portnameLong;
	}
	
	@Override
	public void setPortnameLong(String portnameLong) {
		this.portnameLong = portnameLong;
	}
	
	@Override
	public String getPortnameShort() {
		return portnameShort;
	}
	
	@Override
	public void setPortnameShort(String portnameShort) {
		this.portnameShort = portnameShort;
	}
	
	@Override
	public String getIpaddress() {
		return ipaddress;
	}
	
	@Override
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
		generateTooltipText();
	}
	
	@Override
	public void setStatus(Portstatus status) {
		this.status = status;
		determinePortStatus();
	}
	
	@Override
	public Portstatus getStatus() {
		return status;
	}
	
	@Override
	public Portstatus getProtocolStatus() {
		return protocol;
	}
	
	@Override
	public void setProtocolStatus(Portstatus protocol) {
		this.protocol = protocol;
		determinePortStatus();
	}

	@Override
	public HashMap<String, String> getInterfaceData() {
		return interfaceData;
	}

	@Override
	public String getInterfaceData(String key) {
		return interfaceData.get(key);
	}

	@Override
	public String getTooltipText() {
		return tooltipText.get();
	}

	@Override
	public StringProperty tooltipTextProperty() {
		return tooltipText;
	}

	@Override
	public void generateTooltipText() {
		if(status == null || protocol == null) return;
		String tooltip = "Fullname: "+portnameLong+"\n"
				+"Shortname: "+portnameShort+"\n"
				+"IP-Address: "+ipaddress+"\n"	
				+"Status: "+status+"\n"
				+"Protocol: "+protocol;
		for(String key : interfaceData.keySet().toArray(new String[interfaceData.size()])) {
			if(interfaceData.get(key).equals("no") || interfaceData.get(key).isEmpty());
			else tooltip += "\n"+key+": "+interfaceData.get(key);	
		}
		
		final String tooltipfinal = tooltip;
		Platform.runLater(()->{
			tooltipText.set(tooltipfinal);
		});		
	}

	@Override
	public String getRunningConfig() {
		return runningConfig.get();
	}	

	@Override
	public StringProperty runningConfigProperty() {
		return runningConfig;
	}

	@Override
	public void setRunningConfig(String text) {		
		runningConfig.set(text);
	}

	@Override
	public void addInterfaceData(String key, String value) {
		if(interfaceData.containsKey(key) && interfaceData.get(key).equals(value)) return;
		interfaceData.put(key, value);
		generateTooltipText();
	}

	@Override
	public boolean equals(Object obj) {
		Interface other = (Interface) obj;		
		if (!portnameLong.equals(other.portnameLong))
			return false;
		if (!portnameShort.equals(other.portnameShort))
			return false;		
		return true;
	}

	@Override
	public boolean isBound() {
		return bound;
	}
	
}
