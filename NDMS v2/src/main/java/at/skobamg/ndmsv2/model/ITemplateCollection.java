package at.skobamg.ndmsv2.model;

import java.util.ArrayList;

public interface ITemplateCollection {
	public void setTemplateList(ArrayList<IXMLTemplate> templateList);
	public ArrayList<String> getTemplatesofSwitch(String switchName, String switchVersion);
	public ArrayList<String> getAllTemplates();
}
