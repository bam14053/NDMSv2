package at.skobamg.ndmsv2.model;

import java.util.ArrayList;

public class TemplateCollection implements ITemplateCollection {
	private ArrayList<IXMLTemplate> templateList;
	
	public TemplateCollection(ArrayList<IXMLTemplate> templates) {
		templateList = templates;
	}
	
	@Override
	public ArrayList<String> getTemplatesofSwitch(String switchName,
			String switchVersion) {
		ArrayList<String> templates = new ArrayList<String>();
		for(IXMLTemplate template : templateList)
			if(template.getTemplateInfo().toLowerCase().equals(switchName+"-"+switchVersion))
				templates.add(template.getFilePath());
		return templates;
	}
	
	@Override
	public void setTemplateList(ArrayList<IXMLTemplate> templateList) {
		this.templateList = templateList;
	}

	@Override
	public ArrayList<String> getAllTemplateNames() {
		ArrayList<String> templates = new ArrayList<String>();
		for(IXMLTemplate template : templateList) templates.add(template.getFileName());
		return templates;
	}

}
