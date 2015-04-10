package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITab;
import at.skobamg.ndmsv2.model.Tab;

public class TabsContoller implements ITabsController{
	@Autowired
	private IEventMediator eventMediator;
	private HashMap<String, ITab> tabs = new HashMap<String, ITab>();
	public static int TAB_NORMAL_PRIORITY = 10000;
	public static int TAB_MINIMAL_PRIORITY = 20000;
	public static int TAB_MAXIMAL_PRIORITY = 5000;

	@Override
	public void addTab(String tabName, ArrayList<IInterface> interfaces,
			String[] sessionInfo, ArrayList<String> templates, ITemplate template) {
		tabs.put(tabName, new Tab(tabName, interfaces, sessionInfo, templates, template));
		eventMediator.addTab(tabName, interfaces);
		eventMediator.updateTemplateList(templates);			
	}
}
