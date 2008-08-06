package org.if_itb.client;

import java.util.HashMap;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * The Conference type object
 */
public class AYmsgConference
{
	/**
	 * The buddy in the group
	 */
	private HashMap<String, AYmsgBuddy> buddy = new HashMap<String, AYmsgBuddy>();
	
	/**
	 * The name of the room
	 */
	private String id;

	/**
	 * @return the message
	 */
	
	/**
	 * Message that used in invitation
	 */
	private String message;
	
	/**
	 * Public constructor setting the room name
	 * @param roomId the room name
	 */
	public AYmsgConference(String roomId)
	{
		this.id = roomId;
	}

	/**
	 * @param id the Yahoo! id of the buddy
	 * @param buddy the buddy Object
	 * Add buddy to the conference room
	 */
	public void addBuddy(String id, AYmsgBuddy buddy)
	{
		this.buddy.put(id, buddy);
	}

	/**
	 * @return the buddy in the conference
	 */
	public HashMap<String, AYmsgBuddy> getBuddy()
	{
		return this.buddy;
	}
	
	/**
	 * @return the conference room name
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @return the invitation message
	 */
	public String getMessage()
	{
		return this.message;
	}
	
	/**
	 * Remove the buddy form this conference
	 * @param id the buddy id
	 */
	public void removeBuddy(String id)
	{
		if (this.buddy.containsKey(id))
			this.buddy.remove(id);
	}

	/**
	 * @param buddy the buddy in the conference to set
	 */
	public void setBuddy(HashMap<String, AYmsgBuddy> buddy)
	{
		this.buddy = buddy;
	}
	
	/**
	 * @param set the conference room
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
}
