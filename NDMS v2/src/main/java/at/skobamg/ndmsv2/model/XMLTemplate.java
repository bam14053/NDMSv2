package at.skobamg.ndmsv2.model;

public class XMLTemplate implements IXMLTemplate {
	private String switchName;
	private String switchVersion;
	private String fileName;
	
	public XMLTemplate(String switchName, String switchVersion, String fileName) {
		super();
		this.switchName = switchName;
		this.switchVersion = switchVersion;
		this.fileName = fileName;
	}

	public String getTemplateInfo() {
		return switchName+"-"+switchVersion;
	}

	public String getFileName() {
		return fileName;
	}
	
}
