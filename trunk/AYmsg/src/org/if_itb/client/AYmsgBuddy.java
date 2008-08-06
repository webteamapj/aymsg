package org.if_itb.client;

import java.util.HashMap;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * This Class is Representation of a buddy 
 */
public class AYmsgBuddy
{
	/**
	 * The yahoo id
	 */
	private String id;
	
	/**
	 * Boolean value, true if user online, false if it's not
	 */
	private boolean isOnline=false;
	
	/**
	 * Status for this buddy
	 */
	private int status;
	
	/**
	 * Status away
	 */
	private boolean away = false;
	
	/**
	 * Idle status
	 */
	private boolean idle = false;
	
	/**
	 * Custom status 
	 */
	private boolean customStatus = false;
	
	/**
	 * Custom status String
	 */
	private String strCustomStatus = "";
	
	/**
	 * Idle time in second 
	 */
	private int idleTime= -1;
	
	/**
	 * On SMS status 
	 */
	private boolean onSMS = false;
	
	/**
	 * Unicode in status message
	 */
	private boolean useUnicode = false;
	
	/**
	 * The Conference that user joined 
	 */
	private HashMap<String, AYmsgConference> conference = new HashMap<String, AYmsgConference>();
	
	/**
	 * Group that this object held
	 */
	private HashMap<String, AYmsgGroup> groups = new HashMap<String, AYmsgGroup>()  ;

	/**
	 * @param id the Yahoo! Id
	 */
	public AYmsgBuddy(String id)
	{
		this.id = id;
	}

	/**
	 * @param id the yahoo! Id
	 * @param g the Group object
	 */ 
	public AYmsgBuddy(String id, AYmsgGroup g)
	{
		this.id = id;
		this.groups.put(id,g);
	}
	
	/**
	 * @param id the conference id
	 * @param cf the Conference object
	 * Add conference to buddy
	 */
	public void addConference ( AYmsgConference cf)
	{
		this.conference.put(cf.getId(), cf);
	}
	
	/**
	 * @return the conference
	 * return Conference list
	 */
	public HashMap<String, AYmsgConference> getConference()
	{
		return this.conference;
	}

	/**
	 * @return the groups
	 * Return the groups this object belong
	 */
	public HashMap<String, AYmsgGroup> getGroups()
	{
		return this.groups;
	}

	/**
	 * @return the Yahoo id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @return the idleTime
	 */
	public int getIdleTime()
	{
		return this.idleTime;
	}

	/**
	 * @return the status
	 */
	public int getStatus()
	{
		return this.status;
	}
	
	
	/**
	 * @return the strCustomStatus
	 */
	public String getStrCustomStatus()
	{
		return this.strCustomStatus;
	}

	/**
	 * @return the away
	 */
	public boolean isAway()
	{
		return this.away;
	}

	/**
	 * @return the customStatus
	 */
	public boolean isCustomStatus()
	{
		return this.customStatus;
	}

	/**
	 * @return the idle
	 */
	public boolean isIdle()
	{
		return this.idle;
	}

	public boolean isOnline()
	{
		return this.isOnline;
	}

	/**
	 * @return the onSMS
	 */
	public boolean isOnSMS()
	{
		return this.onSMS;
	}

	/**
	 * @return the useUnicode
	 */
	public boolean isUseUnicode()
	{
		return this.useUnicode;
	}

	public void removeConference(AYmsgConference cf)
	{
		this.conference.remove(cf.getId());
	}

	/**
	 * @param away the away to set
	 */
	public void setAway(boolean away)
	{
		this.away = away;
	}

	/**
	 * @param conference the conference to set
	 * Set conference list
	 */
	public void setConference(HashMap<String, AYmsgConference> conference)
	{
		this.conference = conference;
	}

	/**
	 * @param customStatus the customStatus to set
	 */
	public void setCustomStatus(boolean customStatus)
	{
		this.customStatus = customStatus;
	}

	/**
	 * @param groups the groups to set
	 * set the groups
	 */
	public void setGroups(HashMap<String, AYmsgGroup> groups)
	{
		this.groups = groups;
	}

	/**
	 * @param id the Yahoo id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @param idle the idle to set
	 */
	public void setIdle(boolean idle)
	{
		this.idle = idle;
	}

	/**
	 * @param idleTime the idleTime to set
	 */
	public void setIdleTime(int idleTime)
	{
		this.idleTime = idleTime;
	}

	public void setOnline(boolean isOnline)
	{
		this.isOnline = isOnline;
	}

	/**
	 * @param onSMS the onSMS to set
	 */
	public void setOnSMS(boolean onSMS)
	{
		this.onSMS = onSMS;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

	/**
	 * @param strCustomStatus the strCustomStatus to set
	 */
	public void setStrCustomStatus(String strCustomStatus)
	{
		this.strCustomStatus = strCustomStatus;
	}

	/**
	 * @param useUnicode the useUnicode to set
	 */
	public void setUseUnicode(boolean useUnicode)
	{
		this.useUnicode = useUnicode;
	}
}
