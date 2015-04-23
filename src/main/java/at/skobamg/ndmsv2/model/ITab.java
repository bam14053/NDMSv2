package at.skobamg.ndmsv2.model;

import java.util.ArrayList;
import java.util.HashMap;

import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.Tab.TabSession;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public interface ITab extends ChangeListener<String>{
	public ITemplate getTemplate();
	public void setTemplate(ITemplate template);
	public String getTabName();
	public ArrayList<IInterface> getInterfaces();
	public ArrayList<String> getTemplates();
	public ISnippet getInterfaceSnippet();
	public void setInterfaceSnippet(ISnippet interfaceSnippet);
	public HashMap<String, String> getTabData();
	public TabSession getTabSession();
	public void sendCommandsToSwitch(String commands);
	public String getConsoleText();
	public StringProperty consoleTextProperty();
}
