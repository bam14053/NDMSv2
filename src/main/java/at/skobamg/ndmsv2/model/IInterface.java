package at.skobamg.ndmsv2.model;

import java.util.HashMap;

import javafx.beans.property.StringProperty;

public interface IInterface {
	public String getPortnameLong();
	public void setPortnameLong(String portnameLong);
	public String getPortnameShort();
	public void setPortnameShort(String portnameShort);
	public String getIpaddress();
	public void setIpaddress(String ipaddress);
	public Portstatus getStatus();
	public Portstatus getProtocolStatus();
	void setProtocolStatus(Portstatus protocol);
	public void setStatus(Portstatus status);
	public HashMap<String, String> getInterfaceData();
	public String getInterfaceData(String key);
	public void addInterfaceData(String key, String value);
	public String getPortstatus();
	public void determinePortStatus();
	public StringProperty portstatusProperty();
	public String getTooltipText();
	public StringProperty tooltipTextProperty();
	public void generateTooltipText();
	public String getRunningConfig();
	public StringProperty runningConfigProperty();
	public void setRunningConfig(String text);
}
