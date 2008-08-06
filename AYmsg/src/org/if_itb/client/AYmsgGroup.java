package org.if_itb.client;

import java.util.HashMap;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * This class is Representation of a group in the buddy list
 */
public class AYmsgGroup
{
	/**
	 * The group name
	 */
	private String id;
	
	/**
	 * The group Members 
	 */
	private HashMap<String, AYmsgBuddy> buddy = new HashMap<String, AYmsgBuddy>();

	/**
	 * Public constructor
	 * @param id the group name
	 */
	public AYmsgGroup(String id)
	{
		this.id = id;
	}
	
	/**
	 * Add buddy to group
	 * @param buddy
	 */
	public void addMember(AYmsgBuddy buddy)
	{
		this.buddy.put(buddy.getId(),buddy);
	}

	/**
	 * @return the buddy list
	 */
	public HashMap<String, AYmsgBuddy> getBuddyList()
	{
		return this.buddy;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param b the buddylist to set
	 */
	public void setBuddyList(HashMap<String, AYmsgBuddy> b)
	{
		this.buddy = b;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
}
