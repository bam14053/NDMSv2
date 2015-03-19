package at.skobamg.ndmsv2.service;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import at.skobamg.ndmsv2.model.IXMLTemplate;

public interface ITemplateService {
	public ArrayList<IXMLTemplate> getTemplatesDefault() throws ParserConfigurationException, SAXException, IOException;
}
