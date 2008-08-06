package org.if_itb.client;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * This interface is used as a callback. when event happen. the service will call the method on this interface
 * basicly you create an implementation of this class and regiter it with class AYmsg
 */
public interface IAYmsgCallback
{
	
	/**
	 * Called When Authentication failed
	 */
	public void authFailed();
	
	
	/**
	 * Called when authentication has succeded
	 */
	public void authSucceded();
	
	
	/**
	 * Called when succesfully send Buddy Add Message to Server.
	 * When Add Budy packet sent to server, server replay with ACK
	 * this method is called when that packet arrive
	 * Notice that the buddy object is automaticly added to groupList Object
	 * @param b the Buddy
	 * @param group The group
	 */
	public void buddyAddAck(AYmsgBuddy b, AYmsgGroup group);
	
	/**
	 * Called when buddyId Rejected to add
	 * @param buddyId the id that deny
	 * @param myId my id
	 * @param reason the deny reason
	 */
	public void buddyAuthDeny(String buddyId, String myId, String reason);
		
	/**
	 * Called when buddyId allow us to add them 
	 * @param buddyId
	 * @param myId
	 */
	public void buddyAuthOk(String buddyId, String myId);
	
	/**
	 * Called when some one wants to add us in their buddy list
	 * @param buddyId tha buddy that wants to add us
	 * @param myId our id that wants to be added
	 */
	public void buddyAuthReq(String buddyId, String myId);

	/**
	 * Called when buddy list received
	 * @param groupList ArrayList of Group list whick contain groups. Each groups contain ArrayList of buddy
	 * @param buddyList pair of Yahoo!id -> Buddy Object
	 */
	public void buddyListArrived(HashMap<String,AYmsgGroup> groupList, HashMap<String, AYmsgBuddy> buddyList);


	/**
	 * Called when buddy just log off
	 * @param curBuddy the buddy who just loged off
	 */
	public void buddyLogoff(AYmsgBuddy curBuddy);

	/**
	 * Called when buddy just loged on
	 * @param curBuddy the buddy that just loged on
	 */
	public void buddyLogon(AYmsgBuddy curBuddy);

	/**
	 * Called when Succesfully remove buddy from buddy list on server
	 * @param buddy the buddy to remove
	 * @param group the group to remove from
	 */
	public void buddyRemovedAck(AYmsgBuddy buddy, AYmsgGroup group);

	/**
	 * Called when when buddy status reviced. this mean that buddy list is complete with status
	 * @param groupList ArrayList of Group list whick contain groups. Each groups contain ArrayList of buddy
	 * @param buddyList pair of Yahoo!id -> Buddy Object
	 */
	public void buddyStatusArrived(HashMap<String,AYmsgGroup> groupList, HashMap<String, AYmsgBuddy> buddyList);
	
	/**
	 * Called when Y6_STATUS_UPDATE received
	 * This happen when buddy on buddy list updated their status
	 * Note that the buddy list is automatically updated when this happened
	 * @param curBuddy the Buddy that updated the status
	 */
	public void buddyStatusUpdated(AYmsgBuddy curBuddy);


	/**
	 * Called when buddy logged off from conference room
	 * @param cf
	 */
	public void conferenceLoggedOff(String who,AYmsgConference cf);


	/**
	 * @param the person wo just logged on
	 * @param cf the conference object
	 * Called when logged on to conference room
	 */
	public void conferenceLoggedOn(String who, AYmsgConference cf);


	/**
	 * @param cf The conference object
	 * @param inviter Yahoo! id of the inviter
	 * @param msg The invitation message
	 * Called When new Conference invitation received
	 */
	public void conferenceReceivedInvitation(AYmsgConference cf, String inviter, String msg);


	/**
	 * @param cf the conference object
	 * @param sender the buddy sender object
	 * @param msg the message
	 * Called when new message received form Conference
	 */
	public void conferenceReceivedMessage(AYmsgConference cf, AYmsgBuddy sender, String msg);


	/**
	 * Called when the service is ready.
	 */
	public void onAYmsgReady();


	/**
	 * the same user Called when Disconnected or Error in conection
	 * @param the
	 *            disconnection type. see AymsgType the value is: DC_CON_ERROR
	 *            if error happend or DC_OTHER_LOGED when other logged in with
	 *            happened
	 */
	public void onDisconnected(int type);

	/**
	 * @param message the error Message
	 * Called when error happened or service
	 */
	public void onError(String message);

	/**
	 * Called when a new packet arrive and you want to handle it manually.
	 * @param key The key list
	 * @param value The value list
	 * @param Service The service Type
	 * @param Status The Status Type
	 * @param SessionId The Current Session Id
	 * @return if you set the value true, no further handling method are invoked else the library will
	 * search for the corret handler for the packet
	 */
	public boolean rawPacketHandler(List<String> key, List<String> value,int Service, int Status, int SessionId);


	/**
	 * Called When Incomming message received
	 * @param buddy buddy who sent the message
	 * @param msg the message content
	 */
	public void receivedIncomingMessage(AYmsgBuddy buddy, String msg);


	/**
	 * Called when (typing) notify received
	 * @param buddy buddy who sent the (typing) notify
	 * @param on notify type on (start) or off (stop)
	 */
	public void receivedIncomingNotify(AYmsgBuddy buddy,boolean on);


	/**
	 * Called when offline message received
	 * @param buddy buddy who sent the offline message
	 * @param msg the offline message
	 */
	public void receivedOfflineMessage(AYmsgBuddy buddy, String msg);

}
