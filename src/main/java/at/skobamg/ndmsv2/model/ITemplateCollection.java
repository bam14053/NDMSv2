package at.skobamg.ndmsv2.model;

import java.util.ArrayList;

public interface ITemplateCollection {
	public void setTemplateList(ArrayList<IXMLTemplate> templateList);
	/**
	 * Searches through the {@link ArrayList} of templates and returns the filenames of the correspoding switch
	 * @param switchName The name of the switch
	 * @param switchVersion The IOS version of the switch
	 * @return An {@link ArrayList} of filenames which match the criterium
	 */
	public ArrayList<String> getTemplatesofSwitch(String switchName, String switchVersion);
	public ArrayList<String> getAllTemplateNames();
}
