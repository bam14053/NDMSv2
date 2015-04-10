package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.IInterface;

public interface ITabsController{
	void addTab(String tabName, ArrayList<IInterface> interfaces, String[] sessionInfo, ArrayList<String> templates, ITemplate template);
}
