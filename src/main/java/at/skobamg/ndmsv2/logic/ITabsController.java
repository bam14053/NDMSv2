package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;

import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITab;

public interface ITabsController{
	void addTab(String tabName, ArrayList<IInterface> interfaces, String[] sessionInfo, ArrayList<String> templates, ITemplate template);
	void addTab(ITab tab);
	public ITab getTabByName(String tabName);
	public void addListenerToTab(String tabName);
}
