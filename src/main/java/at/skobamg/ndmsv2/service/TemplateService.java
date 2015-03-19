package at.skobamg.ndmsv2.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import at.skobamg.generator.model.ITemplate;
import at.skobamg.ndmsv2.model.Directory;
import at.skobamg.ndmsv2.model.IXMLTemplate;
import at.skobamg.ndmsv2.model.XMLTemplate;

public class TemplateService implements ITemplateService {
	
	class XMLFileFilter implements FilenameFilter{

		public boolean accept(File dir, String name) {
			if(name.endsWith(".xml"))
				return true;
			return false;
		}
		
	}
	
	public ArrayList<IXMLTemplate> getTemplatesDefault() throws ParserConfigurationException, SAXException, IOException {
		ArrayList<IXMLTemplate> templateList = new ArrayList<IXMLTemplate>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
		for(File file : new File(Directory.vorlagen_directory).listFiles(new XMLFileFilter())){							
			Element element = db.parse(file).getDocumentElement();
			if(element.getNodeName().equals(ITemplate.name))
				templateList.add(new XMLTemplate(element.getAttribute(ITemplate.propertySwitchname), element.getAttribute(ITemplate.propertySwitchversion), file.getName()));
		}
		return templateList;
	}
}
