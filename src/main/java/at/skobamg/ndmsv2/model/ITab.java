package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.util.ArrayList;

import at.skobamg.generator.model.ISnippet;
import at.skobamg.generator.model.ITemplate;
import javafx.beans.value.ChangeListener;

public interface ITab extends ChangeListener<String>{
	public ITemplate getTemplate();
	public void setTemplate(ITemplate template);
	public boolean isLockConsole();
	public void setLockConsole(boolean lockConsole);	
	public ConsoleOutput getConsoleOutput();
	public String getTabName();
	public ArrayList<IInterface> getInterfaces();
	public ArrayList<String> getTemplates();
	public void sendCommand(String command) throws IOException;
	public ISnippet getInterfaceSnippet();
	public void setInterfaceSnippet(ISnippet interfaceSnippet);
	public void refreshTab(long refresh_rate);
}
