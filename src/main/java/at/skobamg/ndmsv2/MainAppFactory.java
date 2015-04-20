package at.skobamg.ndmsv2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import at.skobamg.ndmsv2.logic.ITabsController;
import at.skobamg.ndmsv2.logic.TabsContoller;
import at.skobamg.ndmsv2.mediator.EventMediator;
import at.skobamg.ndmsv2.mediator.IEventMediator;
import at.skobamg.ndmsv2.model.ITemplateCollection;
import at.skobamg.ndmsv2.model.TemplateCollection;
import at.skobamg.ndmsv2.view.HauptfensterController;
import at.skobamg.ndmsv2.view.Windows;


@Configuration
public class MainAppFactory {
	@Bean
	public HauptfensterController hauptfensterController() {
		return (HauptfensterController) Windows.controllerLaden(Windows.MainWindow);
	}
	
	@Bean
	public IEventMediator eventMediator() {
		return new EventMediator();
	}
	@Bean
	public ITemplateCollection templateCollection() {
		return new TemplateCollection(null);
	}
	
	@Bean
	public ITabsController iTabsController() {
		return new TabsContoller();
	}
}
