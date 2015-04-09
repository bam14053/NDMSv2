package at.skobamg.ndmsv2.model;

public class XMLTemplate implements IXMLTemplate {
	private String switchName;
	private String switchVersion;
	private String fileName;
	private String filePath;
	
	public XMLTemplate(String switchName, String switchVersion, String fileName, String filePath) {
		super();
		this.switchName = switchName;
		this.switchVersion = switchVersion;
		this.fileName = fileName;
		this.filePath = filePath;
	}

	public String getTemplateInfo() {
		return switchName+"-"+switchVersion;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String getFilePath() {
		return filePath;
	}
	
}
