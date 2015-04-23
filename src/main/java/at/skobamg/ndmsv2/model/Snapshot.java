package at.skobamg.ndmsv2.model;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Snapshot implements ISnapshot, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5477334726063471091L;
	private String name;
	private String content;
	private DateTime dateCreated;
	private String switchName;
	private String switchVersion;

	public Snapshot(String name, String content,
			DateTime dateCreated, String switchName, String switchVersion) {
		super();
		this.name = name;
		this.content = content;
		this.dateCreated = dateCreated;
		this.switchName = switchName;
		this.switchVersion = switchVersion;
	}

	public String getName() {
		return name;
	}
	
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public String getSwitchName() {
		return switchName;
	}

	public String getSwitchVersion() {
		return switchVersion;
	}
	
}
