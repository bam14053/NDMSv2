package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import com.jcraft.jsch.Channel;
import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.ConsoleOutput;
import at.skobamg.ndmsv2.model.IInterface;

public interface ITabsController{
	void addTab(String tabName, ArrayList<IInterface> interfaces, ConsoleOutput consoleOutput, Channel shellChannel,
			ArrayList<String> templates, ITemplate template);
}
