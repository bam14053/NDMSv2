package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITab;
import at.skobamg.ndmsv2.model.Tab;

public class TabsContoller implements ITabsController, ChangeListener<String>{
	@Autowired
	private IEventMediator eventMediator;
	private String selectedTab;
	private HashMap<String, ITab> tabs = new HashMap<String, ITab>();
	public static int TAB_NORMAL_PRIORITY = 10000;
	public static int TAB_MINIMAL_PRIORITY = 20000;
	public static int TAB_MAXIMAL_PRIORITY = 5000;

	@Override
	public void addTab(String tabName, ArrayList<IInterface> interfaces,
			String[] sessionInfo, ArrayList<String> templates, ITemplate template) {
		tabs.put(tabName, new Tab(tabName, interfaces, sessionInfo, templates, template));			
	}

	@Override
	public void addTab(ITab tab) {
		tabs.put(tab.getTabName(), tab);
	}
	
	@Override
	public ITab getTabByName(String name) {
		return tabs.get(name);
	}

	@Override
	public void addListenerToTab(String tabName) {
		if(selectedTab != null)
			tabs.get(selectedTab).consoleTextProperty().removeListener(this);		
		tabs.get(tabName).consoleTextProperty().addListener(this);		
		selectedTab = tabName;
	}

	@Override
	public void changed(ObservableValue<? extends String> observable,
			String oldValue, String newValue) {
		String line = newValue.split("\n")[newValue.split("\n").length-1];
		if(line.endsWith(selectedTab+">") || 
				line.endsWith(selectedTab+"#")) 
			eventMediator.newCommandLine(line);
		else 
			eventMediator.newMessageLine(line);		
	}
}
