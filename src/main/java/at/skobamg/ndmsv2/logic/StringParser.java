package at.skobamg.ndmsv2.logic;

import java.util.ArrayList;
import java.util.HashMap;

import at.skobamg.generator.model.IInterface;
import at.skobamg.ndmsv2.model.Interface;
import at.skobamg.ndmsv2.model.Portstatus;

public class StringParser {
	public static String extractIOSVersion(String text, String hostname) {
		String[] lines = text.toLowerCase().split("\n");
		for(String line : lines) {
			if(line.contains(hostname.toLowerCase()+">") || line.contains(hostname.toLowerCase()+"#") || line.contains("show version"));
			else if(line.contains("version")) {
				String[] parts = line.split(",");
				for(String part : parts) {
					if(part.contains("version")) {
						for(String parts2 : part.split(" ")) {
							if(parts2.isEmpty());
							else if(parts2.equals("version"));
							else return parts2;
						}
					}	
				}
			}
		}
		return null;
	}
	
	public static String extractSwitchName(String text, String hostname) {
		String[] lines = text.toLowerCase().split("\n");
		for(String line : lines) {
			if(line.contains(hostname.toLowerCase()+">") || line.contains(hostname.toLowerCase()+"#"));
			else if(line.contains("software")) {
				String[] parts = line.split(",");
				for(String part : parts) {
					if(part.contains("software") && !part.contains("cisco")) {
						String[] parts2 = part.split(" ");
						if(parts2[0].isEmpty()) return parts2[1];
						else return parts2[0];
					}
				}
			}
		}
		return null;
	}
	
	private static HashMap<Integer, String> extractHeaders(String consoleText) {
		ArrayList<String> partstemp = new ArrayList<String>();
		for(String temp : consoleText.replace("\r", "").replace(" ", "/").split("/"))
			if(!temp.isEmpty()) partstemp.add(temp);
		String[] parts = partstemp.toArray(new String[partstemp.size()]);
		HashMap<Integer, String> headers = new HashMap<Integer, String>();		
		for(int i = 0; i < parts.length; i++)
			if(!parts[i].isEmpty() && Interface.getHeaders().contains(parts[i]))				
				headers.put(i, parts[i]);
		return headers;
	}

	public static ArrayList<at.skobamg.ndmsv2.model.IInterface> extractInterfaceInformation(ArrayList<IInterface> interfaces, String consoleText, String hostname) {
		String[] lines = consoleText.toLowerCase().split("\n");
		ArrayList<at.skobamg.ndmsv2.model.IInterface> confInterfaces = new ArrayList<at.skobamg.ndmsv2.model.IInterface>();
		HashMap<Integer, String> headers = null;
		
		for(String line : lines) {
			if(line.contains(hostname.toLowerCase()+">") || line.contains(hostname.toLowerCase()+"#") || line.startsWith("show"));
			else if(headers == null && line.contains(Interface.HEADER_INTERFACE)) headers = extractHeaders(line);
			else if(headers != null) break; 
		}
		
		for(IInterface interf : interfaces) {
			if(interf.getPortRange().equals("-")){
				for(String line : lines) {
					if(line.contains(interf.getPortBezeichnunglang().toLowerCase())) {
						at.skobamg.ndmsv2.model.IInterface confInterface = new Interface();
						ArrayList<String> partstemp = new ArrayList<String>();
						for(String temp : line.replace(" ", "-").replace("\b", "").split("-"))
							if(!temp.isEmpty() && !temp.equals("more")) partstemp.add(temp);
						String[] parts = partstemp.toArray(new String[partstemp.size()]);
						for(int i = 0; i < parts.length; i++) {
							if(!headers.containsKey(i)) continue;
							switch(headers.get(i)) {
							case Interface.HEADER_INTERFACE:
								confInterface.setPortnameLong(parts[i]);
								confInterface.setPortnameShort(parts[i].replace(interf.getPortBezeichnunglang().toLowerCase(), interf.getPortBezeichnungkurz().toLowerCase()));
								break;
							case Interface.HEADER_IPADDRESS:
								confInterface.setIpaddress(parts[i]);
								break;
							case Interface.HEADER_PROTOCOL:
								confInterface.setProtocolStatus(Portstatus.valueOf(parts[i]));
								break;
							case Interface.HEADER_STATUS:
								confInterface.setStatus(Portstatus.valueOf(parts[i]));
								break;
							}
						}
						confInterfaces.add(confInterface);
					}
				}
			}else{
				for(String port : parseInterfaceRange(interf.getPortBezeichnunglang().toLowerCase(), interf.getPortRange())) {
					for(String line : lines) {
						if(line.split(" ")[0].equalsIgnoreCase(port)) {
							at.skobamg.ndmsv2.model.IInterface confInterface = new Interface();
							ArrayList<String> stringTemp = new ArrayList<String>();
							for(String temp : line.split(" "))
									if(!temp.isEmpty()) stringTemp.add(temp);
							String[] parts = stringTemp.toArray(new String[stringTemp.size()]);
							for(int i = 0; i < parts.length; i++) {
								if(!headers.containsKey(i)) continue;
								switch(headers.get(i)) {
								case Interface.HEADER_INTERFACE:
									confInterface.setPortnameLong(parts[i]);
									confInterface.setPortnameShort(parts[i].replace(interf.getPortBezeichnunglang().toLowerCase(), interf.getPortBezeichnungkurz().toLowerCase()));
									break;
								case Interface.HEADER_IPADDRESS:
									confInterface.setIpaddress(parts[i]);
									break;
								case Interface.HEADER_PROTOCOL:
									confInterface.setProtocolStatus(Portstatus.valueOf(parts[i]));
									break;
								case Interface.HEADER_STATUS:
									confInterface.setStatus(Portstatus.valueOf(parts[i]));
									break;
								}
							}
							confInterfaces.add(confInterface);
						}
					}
				}
			}
		}
		return confInterfaces;
	}
	
	private static ArrayList<String> parseInterfaceRange(String portnameLong, String portrange) {
		ArrayList<String> ports = new ArrayList<String>();
		if(!portrange.contains("/")) {			
			int first = Integer.parseInt(portrange.split("-")[0]);
			int last = Integer.parseInt(portrange.split("-")[1]);
			while(first <= last) ports.add(portnameLong+"0/"+(first++));
		}else if(!portrange.split("-")[1].contains("/")) {
			int first = Integer.parseInt(portrange.split("/")[0].split("/")[1]);
			int last = Integer.parseInt(portrange.split("-")[1]);
			while(first <= last) ports.add(portnameLong+portrange.charAt(0)+"/"+(first++));
		}else {
			String from = portrange.split("-")[0];
			String to = portrange.split("-")[1];
			int first = Integer.parseInt(from.split("/")[1]);
			int last = Integer.parseInt(to.split("/")[1]);
			while(first <= last) ports.add(portnameLong+portrange.charAt(0)+"/"+(first++));
		}
		return ports;
	}

	public static String extractHostname(String consoleText) {
		String[] lines = consoleText.split("\n");
		for(String line : lines) {
			if(line.contains(">")) {
				return line.split(">")[0];				
			}else if (line.contains("#"))
				return line.split("#")[0];
		}
		return null;
	}
}
