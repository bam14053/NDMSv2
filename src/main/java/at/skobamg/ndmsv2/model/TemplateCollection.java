package at.skobamg.ndmsv2.model;

import java.util.ArrayList;

public class TemplateCollection implements ITemplateCollection {
	private ArrayList<IXMLTemplate> templateList;
	
	public TemplateCollection(ArrayList<IXMLTemplate> templates) {
		templateList = templates;
	}
	
	public ArrayList<String> getTemplatesofSwitch(String switchName,
			String switchVersion) {
		ArrayList<String> templates = new ArrayList<String>();
		for(IXMLTemplate template : templateList)
			if(template.getTemplateInfo().equals(switchName+"-"+switchVersion))
				templates.add(template.getFileName());
		return templates;
	}

	public ArrayList<String> getAllTemplates() {
		ArrayList<String> templates = new ArrayList<String>();
		for(IXMLTemplate template : templateList) {
			templates.add(template.getFileName());
		}
		return templates;
	}

	public void setTemplateList(ArrayList<IXMLTemplate> templateList) {
		this.templateList = templateList;
	}

}
