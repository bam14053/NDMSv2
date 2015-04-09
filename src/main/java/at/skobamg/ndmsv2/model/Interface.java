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
	
	public final String getPortstatus() { 
		return portstatus.get(); 
	}
	
	private void setPortStatus(String status) {		
		portstatus.set(status);			
		if(tooltipText.isBound())
			generateTooltipText();
	}
	
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
			}
			break;
		case down:
			setPortStatus("-fx-base: #954535;");
			break;
		}
	}
	
	public StringProperty portstatusProperty() {
		return portstatus;
	}
	
	public String getPortnameLong() {
		return portnameLong;
	}
	
	public void setPortnameLong(String portnameLong) {
		this.portnameLong = portnameLong;
	}
	
	public String getPortnameShort() {
		return portnameShort;
	}
	
	public void setPortnameShort(String portnameShort) {
		this.portnameShort = portnameShort;
	}
	
	public String getIpaddress() {
		return ipaddress;
	}
	
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
		generateTooltipText();
	}
	
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
		String tooltip = "Fullname: "+portnameLong+"\n"
				+"Shortname: "+portnameShort+"\n"
				+"IP-Address: "+ipaddress+"\n"
				+"Status: "+status+"\n"
				+"Protocol: "+protocol;
		for(String key : interfaceData.keySet().toArray(new String[interfaceData.size()]))
			tooltip += "\n"+key+": "+interfaceData.get(key);	
		tooltipText.set(tooltip);		
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

	
	
}
