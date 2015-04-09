package at.skobamg.ndmsv2.logic;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.jcraft.jsch.Channel;

import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.ConsoleOutput;
import at.skobamg.ndmsv2.model.IInterface;
import at.skobamg.ndmsv2.model.ITab;
import at.skobamg.ndmsv2.model.Tab;

public class TabsContoller implements ITabsController{
	@Autowired
	private IEventMediator eventMediator;
	private HashMap<String, ITab> tabs = new HashMap<String, ITab>();
	public static int TAB_WAIT_PERIOD = 5000;

	@Override
	public void addTab(String tabName, ArrayList<IInterface> interfaces, ConsoleOutput consoleOutput,
			Channel shellChannel, ArrayList<String> templates,
			ITemplate template) {
		tabs.put(tabName, new Tab(tabName, interfaces, consoleOutput, shellChannel, templates, template));
		eventMediator.addTab(tabName, interfaces);
		eventMediator.updateTemplateList(templates);
	}

}
