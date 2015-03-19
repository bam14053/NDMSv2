package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import at.skobamg.ndmsv2.model.IXMLTemplate;
import at.skobamg.ndmsv2.service.TemplateService;

public class LoadTemplatesCommand extends Service<ArrayList<IXMLTemplate>> {

	@Override
	protected Task<ArrayList<IXMLTemplate>> createTask() {
		return new Task<ArrayList<IXMLTemplate>>() {
			
			@Override
			protected ArrayList<IXMLTemplate> call() throws Exception {
				return new TemplateService().getTemplatesDefault();
			}
		};
	}

}
