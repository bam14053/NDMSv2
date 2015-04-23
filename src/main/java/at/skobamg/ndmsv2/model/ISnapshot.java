/**
 * 
 */
package at.skobamg.ndmsv2.model;

import org.joda.time.DateTime;

/**
 * @author abideen
 *
 */
public interface ISnapshot {
	public String getName();
	public String getContent();
	public void setContent(String content);
	public DateTime getDateCreated();
	public String getSwitchName();
	public String getSwitchVersion();
}
