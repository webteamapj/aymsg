package org.if_itb.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.if_itb.aymsg.AYmsgBodyBuffer;
import org.if_itb.aymsg.AYmsgLib;
import org.if_itb.aymsg.IAYmsgService;
import org.if_itb.aymsg.IAYmsgServiceCallback;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * This class is the main library to communicate to service part of the android
 */
 
public class AYmsg
{
	protected static final int DELAY = 30000;
	
	/**
	 * For Log purpose
	 */
	private final String TAG = "AYmsg";
	
	private boolean autoReconnect = false;
	
	/**
	 * Service Interface
	 */
	private IAYmsgService mService = null;
	
	
	/**
	 * Activity that instantiate this class
	 */
	private Activity activity = null;
	
	/**
	 * The callback function
	 */
	private IAYmsgCallback clientCallback = null;
	
	
	/**
	 * Current seesion ID
	 */
	@SuppressWarnings("unused")
	private int SessionId =0;
	
	/**
	 * Username or Yahoo! Id
	 */
	private String username="";
	/**
	 * The Password
	 */
	private String password="";
	
	/**
	 * The Representation of buddy of the user
	 */
	private AYmsgBuddy me;
	
	/**
	 * List of group
	 */
	private HashMap<String, AYmsgGroup> groupList = new HashMap<String, AYmsgGroup>();
	
	/**
	 * List of buddy List
	 */
	private HashMap<String, AYmsgBuddy> buddyList = new HashMap<String, AYmsgBuddy>();
	/**
	 * List of conference
	 */
	private HashMap<String, AYmsgConference> conferenceList = new HashMap<String, AYmsgConference>();
	/**
	 * Used to connect to service
	 */
	private ServiceConnection mConnection = new 
	ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.d(AYmsg.this.TAG, "Connected");
			AYmsg.this.mService = IAYmsgService.Stub.asInterface(service);
			try
			{
				AYmsg.this.mService.registerCallback(AYmsg.this.mCallback);
				if (AYmsg.this.clientCallback != null)
					AYmsg.this.clientCallback.onAYmsgReady(); //callback service ready
					
			} catch (DeadObjectException e)
			{

				e.printStackTrace();
			}
			Toast.makeText(AYmsg.this.activity, R.string.service_stared,
					Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className)
		{
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, if its process crashed.
			try
			{
				AYmsg.this.mService.unregisterCallback(AYmsg.this.mCallback);
			} catch (DeadObjectException e)
			{
				e.printStackTrace();
			}
			AYmsg.this.mService = null;
			// As part of the sample, tell the user what happened.
			Toast.makeText(AYmsg.this.activity, R.string.service_disconnected,
					Toast.LENGTH_SHORT).show();
		}
	};
	
	

	/**
	 * The callback from service
	 */
	private IAYmsgServiceCallback mCallback = new IAYmsgServiceCallback.Stub() {
		
		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgServiceCallback#rawPacketHandler(java.util.List, java.util.List, int, int, int)
		 */
		public void rawPacketHandler(List<String> key, List<String> value,
				int service, int status, int sessionId2)
				throws DeadObjectException
		{
			boolean handled = false;
			Log.i(AYmsg.this.TAG,"AYmsg.rawPacketHandle : invoked");
			
			AYmsg.this.SessionId = sessionId2;
			
			if (AYmsg.this.clientCallback != null)
			{
				handled = AYmsg.this.clientCallback.rawPacketHandler(key, value,service, status,AYmsg.this.SessionId);
			}
			if (handled)
				return;
		
			//check For Authentication Package
			if (service == AYmsgType.AUTH && status == AYmsgType.YAHOO_STATUS_BRB)
			{
				AYmsg.this.handleAuth(value.get(1));
			}
			
			//handle fail authenticate
			if (service == AYmsgType.AUTHRESP && status == AYmsgType.YAHOO_STATUS_DISCONNECTED)
			{
				AYmsg.this.handleAuthFail();
			}
			
			//Check for List (only cookie n stuff)
			if (service == AYmsgType.LIST)
			{
				//Just ignore for now
			}
			
			// Check for Buddy list
			if (service == AYmsgType.LIST_15)
			{
				AYmsg.this.handleBuddyList15(key,value);
			}
			
			// Handle Yahoo List of online buddy
			if (service == AYmsgType.STATUS_15)
			{
				AYmsg.this.handleStatusList15(key,value);
			}
			
			//Handle ACK packet Add
			if ((service == AYmsgType.FRIENDADD) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleAddedBuddyAck(key,value);
			}

			// handle Y7 Buddy Authorization
			if ((service == AYmsgType.Y7_AUTHORIZATION) && (status == AYmsgType.YAHOO_STATUS_NOTATHOME))
			{
				AYmsg.this.handleBuddyAuthReq(key, value);
			}
			
			//handle Y7 Buddy Authorization added
			if ((service == AYmsgType.Y7_AUTHORIZATION) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleBuddyAuthOk(key, value);
			}
			
			
			//Handle update status buddy
			if ((service == AYmsgType.Y6_STATUS_UPDATE) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleStatusUpdate(key,value);
			}
			
			//handle ACK packet Buddy Remove
			if ((service == AYmsgType.FRIENDREMOVE) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleRemoveBuddyAck(key,value);
			}
			
			//handle Pager Logon - user just log on
			if ((service == AYmsgType.LOGON) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleBuddyLogOn(key,value);
			}
			
			//handle Pager Logoff - user just log off
			if ((service == AYmsgType.LOGOFF) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleBuddyLogOff(key,value);
			}
			
			//handle Notify
			if ((service == AYmsgType.NOTIFY) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleReceivedNotify(key,value);
			}
			
			//handle Incomming Message
			if ((service == AYmsgType.MESSAGE) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleReceivedMessage(key,value);
			}
			
			//handle offline Message
			if ((service == AYmsgType.MESSAGE) && (status == AYmsgType.YAHOO_STATUS_NOTINOFFICE))
			{
				AYmsg.this.handleReceivedOffMessage(key,value);
			}
			
			
			//handle logOn to conference
			if ((service == AYmsgType.CONFLOGON) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleConferenceLogOn(key,value);
			}
			
			//handle logOff to conference
			if ((service == AYmsgType.CONFLOGOFF) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleConferenceLogOff(key,value);
			}
			
			//handle received conference message
			if ((service == AYmsgType.CONFMSG) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleReceivedConferenceMessage(key,value);
			}
			
			//handle conference Invitation
			if ((service == AYmsgType.CONFADDINVITE) && (status == AYmsgType.YAHOO_STATUS_SERVER_ACK))
			{
				AYmsg.this.handleReceivedConferenceInvitation(key,value);
			}
			
			//handle home made error
			if (service == AYmsgType.AYMSG_ERROR)
			{
				AYmsg.this.handleAymsgError(key,value);
			}
			
			//handle on disconnect
			if (service == AYmsgType.DC_CON_ERROR && status == AYmsgType.YAHOO_STATUS_DISCONNECTED)
			{
				handleDisconnect(key,value);
			}
			
			/*//handle loged on somewhere
			if (service == AYmsgType.DC_OTHER_LOGED && status == AYmsgType.YAHOO_STATUS_DISCONNECTED)
			{
				List<String> cKey = new ArrayList<String>();
				cKey.add("1");
				List<String> cVal = new ArrayList<String>();
				cVal.add(String.valueOf(AYmsgType.DC_OTHER_LOGED));
				handleDisconnect(cKey, cVal);
			}*/
		}

		
	};

	/**
	 * The timer ping 
	 */
	protected Timer tPing = null;

	/**
	 * @param activity the activity that instantiate this class
	 * the main constructor. always use this constructor
	 */
	public AYmsg(Activity activity)
	{
		this.activity = activity;
	
	}

	/**
	 * Start timer to ping the Yahoo! server so the service is not disconnected
	 */
	private void startPingTimer()
	{
		this.tPing = new Timer(true);
		TimerTask taskPing = new TimerTask()
		{

			/* (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run()
			{
				Log.d(AYmsg.this.TAG,"Ping the server");
				try
				{
					AYmsg.this.mService.sendPacket(AYmsgType.PING, AYmsgType.YAHOO_STATUS_DEFAULT, 0, null);
				} catch (DeadObjectException e)
				{
					e.printStackTrace();
				}
			}
			
		};
		this.tPing.scheduleAtFixedRate(taskPing, DELAY, DELAY); //start every 30 second
	}
	
	/**
	 * To turn off the timer ping
	 */
	@SuppressWarnings("unused")
	private void stopPingTimer()
	{
		if (this.tPing != null)
			this.tPing.cancel();
	}
	
	/**
	 * Add new buddy into group
	 * @param myId The User Yahoo! Id. can get this with getUsername()
	 * @param yahooId the friend Yahoo! Id
	 * @param group The group to be inserted into
	 * @throws IllegalArgumentException when one or more argument is empty
	 * @throws IllegalStateException when not logged in
	 */
	public void addBuddy(String myId, String yahooId, String group) throws IllegalArgumentException, IllegalStateException
	{
		this.checkLoginState();
		
		//Throw exception if user try to set custom status
		if ((yahooId == null)  || (yahooId.length() == 0) || (group == null)|| (group.length() == 0) || (myId == null) || (myId.length() == 0))
			throw (new IllegalArgumentException("Argument cannot empty"));
		
		final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		try
		{
			body.addElement("14", ""); // ???: effective id?
			body.addElement("65", group);
			body.addElement("97", "1");
			body.addElement("1", myId); // ???: effective id?
			body.addElement("302", "319"); // ???: effective id?
			body.addElement("300", "319"); // ???: effective id?
			body.addElement("7", yahooId);
			body.addElement("301", "319"); // ???: effective id?
			body.addElement("303", "319"); // ???: effective id?
			
			this.mService.sendPacket(AYmsgType.FRIENDADD, AYmsgType.YAHOO_STATUS_DEFAULT, this.SessionId, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Authenticate or let buddyId to add myId in their buddy lsit
	 * @param buddyId the buddy to authorize
	 * @param myId yahoo! id to authorize
	 * @throws IllegalArgumentException called when one or more parameter is empty
	 * @throws IllegalStateException called when not logged in
	 * @throws UnsupportedEncodingException
	 * @throws IOException when not connected
	 */
	public void authBuddy(String buddyId, String myId) throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException, IOException
	{
		this.checkLoginState();
		
		if (buddyId == null || buddyId.length() == 0 || myId== null || myId.length() == 0)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", myId); //the username
		body.addElement("5", buddyId); //the id to loginto (currently only support for main)
		body.addElement("241", "0"); //dont know
		body.addElement("13", "1"); //dont know my guest is 1 = auth, 2 = deny
		body.addElement("334", "0"); //dont know

		try
		{
			this.mService.sendPacket(AYmsgType.Y7_AUTHORIZATION, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Check the Current State of connection. throw illegal State Exception if not connected
	 * @throws IllegalStateException when not connected
	 */
	public void checkLoginState() throws IllegalStateException 
	{
		boolean connected = false;
		try
		{
			if (this.mService.getState() == AYmsgLib.CONN_LOGGED)
				connected = true;
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
		if (!connected)
			throw (new IllegalStateException("Not Logged in"));
	}

	/**
	 * Join Conference from invitation. Invitation message contain users in the room.
	 * to join the room we must login using that information
	 * @param cf Conference object
	 */
	public void conferenceJoinInvitation(AYmsgConference cf) throws IllegalStateException,IOException
	{
		this.checkLoginState();
		
		if (cf == null)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", this.getUsername()); //the username
		body.addElement("3", this.getUsername()); //Log in my username
		
		//include from invitation
		String names[] = new String [cf.getBuddy().size()];
		cf.getBuddy().keySet().toArray(names);
		
		for (String name : names)
		{
			body.addElement("3", name); //the id to loginto (currently only support for main)
		}
		
		body.addElement("57", cf.getId()); //the room to log into
		
		try
		{
			this.mService.sendPacket(AYmsgType.CONFLOGON, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * Logoff from a Conference
	 * @param myId My Yahoo! Id
	 * @param cf the conference room object
	 * @throws IllegalStateException throw when one or more parameter are empty
	 * @throws IOException 
	 */
	public void conferenceLogOff(String myId, AYmsgConference cf) throws IllegalStateException,IOException
	{
		
		this.checkLoginState();
		
		if (myId == null || myId.length() == 0 || cf == null)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", this.getUsername()); //the username
		body.addElement("3", this.getUsername()); //the id to loof (currently only support for main)
		
		HashMap<String, AYmsgBuddy> buddyListOnCf = cf.getBuddy();
		if (buddyListOnCf != null && buddyListOnCf.size() > 0)
		{
			String buddyName[] = new String[buddyListOnCf.size()];
			buddyListOnCf.keySet().toArray(buddyName);
			for (String name : buddyName)
			{
				body.addElement("3", name);
				
				//remove conference from buddy
				AYmsgBuddy b = buddyListOnCf.get(name);
				//remove buddy from conference
				cf.removeBuddy(b.getId());
				b = null;
			}
		}
		
		body.addElement("57", cf.getId()); //the room to log into
		this.conferenceList.remove(cf.getId()); //remove conference
		cf = null;
		
		try
		{
			this.mService.sendPacket(AYmsgType.CONFLOGOFF, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * Function to log in into conference room
	 * @param myId My user id
	 * @param room room to log into
	 * @throws IllegalStateException throws when one or more paramter is empty
	 */
	public void conferenceLogOn(String myId, String room) throws IllegalStateException,IOException
	{
		
		this.checkLoginState();
		
		if (myId == null || myId.length() == 0 || room == null || room.length() == 0)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", this.getUsername()); //the username
		body.addElement("3", this.getUsername()); //the id to loginto (currently only support for main)
		body.addElement("57", room); //the room to log into
		
		try
		{
			this.mService.sendPacket(AYmsgType.CONFLOGON, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * Send invitation to users
	 * @param user invitation user destination
	 * @param room room name
	 * @param msg invitation Message
	 * @throws IllegalStateException throw when we are not in login mode
	 * @throws IllegalArgumentException throw when one or more argument is null
	 * @throws IOException
	 */
	public void conferenceSendInvitation(String user, AYmsgConference cf, String msg ) throws IllegalStateException, IllegalArgumentException,IOException
	{
		//Log.d(TAG,"conferenceSendInvitation > user: " +user + " msg: " + msg + " cf: " + cf);
		this.checkLoginState();
		
		if (user == null || user.length() == 0 || cf == null)
			throw (new IllegalArgumentException("One or more argument is empty"));
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", this.getUsername()); //the username
		body.addElement("51", user); //the buddy to invite
		body.addElement("57", cf.getId()); //the room to log into
		body.addElement("58", msg); //the room to log into
		body.addElement("13", "0"); //Dont know what for
		
		//add users in the conference
		if (cf.getBuddy() != null && cf.getBuddy().size() > 0)
		{	
			String members[] = new String[cf.getBuddy().size()];
			cf.getBuddy().keySet().toArray(members);

			for (String member : members)
			{
				body.addElement("52", member); //the member in the conference
				body.addElement("53", member); //the member in the conference
			}
		}
		
		//send packet
		try
		{
			this.mService.sendPacket(AYmsgType.CONFADDINVITE, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Send message to spesific Conference
	 * @param cf the conference object
	 * @param message message to send
	 * @throws IllegalStateException throw when not on logged in state
	 * @throws IOException
	 */
	public void conferenceSendMessage(AYmsgConference cf, String message ) throws IllegalStateException,IOException
	{
		this.checkLoginState();
		
		if (cf == null || message == null)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", this.getUsername()); //the username
		
		//include from invitation
		String names[] = new String [cf.getBuddy().size()];
		cf.getBuddy().keySet().toArray(names);
		
		for (String name : names)
		{
			body.addElement("53", name); //the id buddy in the conference
		}
		
		body.addElement("57", cf.getId()); //the room to log into
		body.addElement("14", message); //the Message
		body.addElement("97", "1"); //Don't know
		
		
		try
		{
			this.mService.sendPacket(AYmsgType.CONFMSG, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Deny request budy Authentication. Deny buddyId to add myId as friend
	 * @param buddyId the buddy who wants to add myId
	 * @param myId my Yahoo! id to add
	 * @param reason reason to deny
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public void denyBuddy(String buddyId, String myId, String reason) throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException, IOException
	{
		this.checkLoginState();
		
		if (buddyId == null || buddyId.length() == 0 || myId== null || myId.length() == 0)
		{
			throw (new IllegalStateException("One or more parameter is empty"));
		}
		
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", myId); //the username
		body.addElement("5", buddyId); //the id to deny
		body.addElement("13", "2"); //dont know
		body.addElement("334", "0"); //dont know
		body.addElement("97", "1"); //dont know
		body.addElement("14", reason); //reason to deny

		try
		{
			this.mService.sendPacket(AYmsgType.Y7_AUTHORIZATION, AYmsgType.YAHOO_STATUS_DEFAULT	, 0, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return the buddyList
	 */
	public HashMap<String, AYmsgBuddy> getBuddyList()
	{
		return this.buddyList;
	}
	
	/**
	 * @return the conferenceList
	 */
	public HashMap<String, AYmsgConference> getConferenceList()
	{
		return this.conferenceList;
	}

	/**
	 * Get Array of group list
	 * @return the groupList
	 */
	public HashMap<String,AYmsgGroup> getGroupList()
	{
		return this.groupList;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return this.username;
	}
	
	/**
	 * @return the autoReconnect
	 */
	public boolean isAutoReconnect()
	{
		return this.autoReconnect;
	}
	
	/**
	 * Do Login to Yahoo! Messenger Server
	 * @param username the Yahoo! Id
	 * @param password the password
	 */
	public void login(String username, String password)
	{
		this.username = username;
		this.password = password;
		Log.i(this.activity.getString(R.string.TAG), "AYmsg: Login");
		try
		{
			this.mService.login(username, password);
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Logout from Yahoo! Messenger server
	 */
	public void logout()
	{
		try
		{
			//log out from all conference
			String names[] = new String[this.conferenceList.size()];
			this.conferenceList.keySet().toArray(names);
			
			if (names != null)
			{
				for (String name : names)
				{
					try
					{
						this.conferenceLogOff(this.getUsername(), this.conferenceList.get(name));
					} catch (IllegalStateException e)
					{
						e.printStackTrace();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			//clear resouce
			if (this.groupList != null && this.groupList.size() > 0)
			{
				this.groupList.clear();
				this.groupList = null;
			}
			if (this.buddyList != null && this.buddyList.size() > 0)
			{
				this.buddyList.clear();
				this.buddyList = null;
			}
			if (this.conferenceList != null && this.conferenceList.size() > 0)
			{
				this.conferenceList.clear();
				this.conferenceList = null;
			}
			this.stopPingTimer();
			this.mService.stopThread();
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This function used to register the callback class 
	 * @param cb Kelas callback implementasi interface AYmsgCallback
	 */
	public void registerClientCallback(IAYmsgCallback cb)
	{
		this.clientCallback = cb;
	}

	/**
	 * Remove Buddy from a group
	 * @param myId primary id
	 * @param yahooId friend Yahoo! id
	 * @param group the group to remove from
	 * @throws IllegalArgumentException when one or more parameter is empty
	 * @throws IllegalStateException when not logged in
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws DeadObjectException 
	 */
	public void removeBuddy(String myId, String yahooId, String group) throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException, IOException, DeadObjectException
	{
		this.checkLoginState();
		
		//Throw exception if user try to set custom status
		if ((yahooId == null)  || (yahooId.length() == 0) || (group == null)|| (group.length() == 0) || (myId == null) || (myId.length() == 0))
			throw (new IllegalArgumentException("Argument cannot empty"));
		
		final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", myId); // ???: effective id?
		body.addElement("7", yahooId);
		body.addElement("65", group);
		this.mService.sendPacket(AYmsgType.FRIENDREMOVE, AYmsgType.YAHOO_STATUS_DEFAULT, this.SessionId, body.getBuffer());
	}

	/**
	 * Remove a group and remove all body contains in the group
	 * @param group the group to remove
	 */
	public void removeGroup(String group) throws IllegalArgumentException , IllegalStateException
	{
		this.checkLoginState();
		
		AYmsgGroup glist = this.groupList.get(group);
		if (glist == null)
		{
			throw (new IllegalArgumentException("No such group"));
		}
		else
		{
			HashMap<String, AYmsgBuddy> tmpBuddys = glist.getBuddyList();
			for (Iterator<String> i = tmpBuddys.keySet().iterator(); i.hasNext();)
			{
				try
				{
					this.removeBuddy(this.getUsername(), tmpBuddys.get(i.next()).getId(), group);
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				} catch (DeadObjectException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Send message to other buddy
	 * @param from user id
	 * @param to buddy id
	 * @param msg message to send
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public void sendMessage(String from, String to, String msg)
	throws IOException, IllegalStateException {
		
		this.checkLoginState();
		
		if (to == null || to.length() == 0 || from == null || from.length() == 0) {
			throw new IllegalArgumentException("One of the argument is empty");
		}

		//Send packet
		AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("1", from); // From (effective ID)
		body.addElement("5", to); // To
		body.addElement("97", "1"); // Dont know
		body.addElement("14", msg); // Dont know
		body.addElement("63", ";0"); // Dont know
		body.addElement("64", "0"); // Dont know
		body.addElement("1002", "1"); // Dont know
		body.addElement("206", "0"); // Dont know
		
		
		try
		{
			this.mService.sendPacket(AYmsgType.MESSAGE, AYmsgType.YAHOO_STATUS_OFLINE, 0, body.getBuffer());
			Log.d(this.TAG,"Send " + msg + " to " + to);
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * Send Typing notify to friend
	 * @param username Primary Yahoo! id (getUsername)
	 * @param friend friend's Yahoo! id
	 * @param on boolean, typing on or of
	 * @param msg The Message
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public void sendNotify(String username, String friend, boolean on,
			String msg) throws IOException,IllegalStateException 
	{
		this.checkLoginState(); 
		
		final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		body.addElement("4", username);
		body.addElement("5", friend);
		body.addElement("14", msg);
		if (on) {
			body.addElement("13", "1");
		} else {
			body.addElement("13", "0");
		}
		body.addElement("49", "TYPING");
		try
		{
			this.mService.sendPacket(AYmsgType.NOTIFY, AYmsgType.YAHOO_STATUS_TYPING, this.SessionId	, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}	
	}

	/**
	 * @param autoReconnect the autoReconnect to set
	 */
	public void setAutoReconnect(boolean autoReconnect)
	{
		this.autoReconnect = autoReconnect;
	}

	/**
	 * @param buddyList the buddyList to set
	 */
	public void setBuddyList(HashMap<String, AYmsgBuddy> buddyList)
	{
		this.buddyList = buddyList;
	}

	/**
	 * @param conferenceList the conferenceList to set
	 */
	public void setConferenceList(HashMap<String, AYmsgConference> conferenceList)
	{
		this.conferenceList = conferenceList;
	}

	/**
	 * Set User CUstom Status with spesicif string
	 * @param status the status code
	 * @param text The status text
	 */
	public void setCustomStatus(int status, String text) throws IllegalStateException
	{
		this.checkLoginState();		
		
		final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		
		try
		{
			body.addElement("19", text);
			body.addElement("10", "99");
			
			if ((status > AYmsgType.YAHOO_STATUS_AVAILABLE) && (status <AYmsgType.YAHOO_STATUS_INVISIBLE) && (status != AYmsgType.YAHOO_STATUS_ONPHONE) && (status != AYmsgType.YAHOO_STATUS_BUSY))
			{
				body.addElement("47", "1");
			}
			if (status == AYmsgType.YAHOO_STATUS_BUSY || status == AYmsgType.YAHOO_STATUS_ONPHONE)
			{
				body.addElement("47", "2");
			}
			
			this.mService.sendPacket(AYmsgType.Y6_STATUS_UPDATE, AYmsgType.YAHOO_STATUS_DEFAULT, this.SessionId, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param groupList the groupList to set
	 */
	public void setGroupList(HashMap<String,AYmsgGroup> groupList)
	{
		this.groupList = groupList;
	}

	/**
	 * Constant are in AYmsgType.YAHOO_STATUS_ ...
	 * @param status the status
	 * @throws IllegalAccessException when status is YAHOO_STATUS_CUSTOM
	 * @throws Exception When the status is YAHOO_STATUS_CUSTOM, use setCustomStatus instead 
	 * Set yahoo Status that already predifined
	 */
	public void setStatus(int status) throws IllegalStateException, IllegalAccessException
	{
		//Throw exception if user try to set custom status
		if (status == AYmsgType.YAHOO_STATUS_CUSTOM)
			throw (new IllegalAccessException("Cant use this method to set custom message"));
	
		this.checkLoginState();
		
		final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
		try
		{
			body.addElement("47", "1");
			body.addElement("19", "");
			body.addElement("10", String.valueOf(status));
			this.mService.sendPacket(AYmsgType.Y6_STATUS_UPDATE, AYmsgType.YAHOO_STATUS_DEFAULT, this.SessionId, body.getBuffer());
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Start the Service back end
	 */
	public void startService()
	{
		this.activity.bindService(new Intent(IAYmsgService.class.getName()),
				this.mConnection, Context.BIND_AUTO_CREATE);
		Log.d(this.TAG, "Starting the service");
	}

	/**
	 * Stop the service back end
	 */
	public void stopService()
	{
		
		this.activity.unbindService(this.mConnection);
	}

	/**
	 * This function user to unregister callback
	 */
	public void unRegisterClientCallback()
	{
		this.clientCallback = null;
	}

	
	/**
	 * Handle when Server ack ADD buddy
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleAddedBuddyAck(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			String myId="";
			String buddyId="";
			String group="";
			
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 1:
						myId = tValue.get(c);
						break;
					case 7:
						buddyId = tValue.get(c);
						break;
					case 65:
						group = tValue.get(c);
						break;
					case 66: /* dont know what for */
					case 23: /* dont know what for */
						break;
				}
				c++;
			}
			
			//ignore if one is empty
			if (myId.length() == 0 || buddyId.length() == 0 || group.length() == 0)
				return;
						
			AYmsgGroup tGroup = this.groupList.get(group);
			
			if (tGroup == null) //new Group
			{
				tGroup = new AYmsgGroup(group);
				this.groupList.put(group, tGroup);
			}
			
			//update List
			
			AYmsgBuddy b = this.buddyList.get(buddyId); //check maybe is on list but no group
			
			if (b == null)//not found then create new buddy
				b = new AYmsgBuddy(buddyId,tGroup);
			
			//add buddy into group
			tGroup.addMember(b);
			this.buddyList.put(buddyId, b);
			
			//add group into buddy
			b.getGroups().put(tGroup.getId(), tGroup);
			
			//call callbacks
			this.clientCallback.buddyAddAck(b,tGroup);
			
		}
	}

	
	/**
	 * Handle Authentication Method, generate Encrypted String from cahllange and send Replay packet
	 * @param challange
	 */
	protected void handleAuth(String challange)
	{
		//getCrumb(username, password, challange);
		try
		{
			InputStream io = this.activity.getResources().openRawResource(R.raw.challenge);
			String test[] = org.openymsg.network.challenge.ChallengeResponseV10.getStrings(this.username,this.password, challange,io);
			Log.i(this.TAG,"challenge 1 : " + test[0]);
			Log.i(this.TAG,"challenge 1 : " + test[1]);
			
			final AYmsgBodyBuffer body = new AYmsgBodyBuffer();
			body.addElement("0", this.username);
			body.addElement("6", test[0]);
			body.addElement("6", test[1]);
			body.addElement("135", "6,0,0,1710"); // Needed for v12(?)
			body.addElement("2", "1");
			body.addElement("1", this.username);
			
			try
			{
				this.mService.sendPacket(AYmsgType.AUTHRESP, 0x86, 0x00, body.getBuffer());
			} catch (DeadObjectException e)
			{
				e.printStackTrace();
			}
			
			
			
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			Log.e(this.TAG,"error : ",e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handle when authentication Fail
	 */
	protected void handleAuthFail()
	{
		this.clientCallback.authFailed();		
	}
	
	/**
	 * Handle home made error message
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleAymsgError(List<String> key, List<String> value)
	{
		if (value != null)
		{
			String error = value.get(0);
			this.clientCallback.onError(error);
		}
			
	}

	
	/**
	 * Called when succesfully add buddy
	 * @param key The Key ID 
	 * @param value The value
	 */
	protected void handleBuddyAuthOk(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			String myId="";
			String buddyId="";
			String reason="";
			boolean ok = false;
			
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 4: // the id that reject or authorize
						buddyId = tValue.get(c);
						break;
					case 5:
						myId = tValue.get(c); // id that request
						break;
					case 13: // "1" = authorize "2" for deny
						ok = (tValue.get(c).equals("1")) ? true : false;
						break;
					case 14: //reason
						reason = tValue.get(c);
						break;
					default :
						break;
				}
				c++;
				if (ok)
					this.clientCallback.buddyAuthOk(buddyId, myId);
				else
					this.clientCallback.buddyAuthDeny(buddyId, myId,reason);
			}
		}
	}

	
	/**
	 * Called when another buddy wants to add us as buddy
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleBuddyAuthReq(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			String myId="";
			String buddyId="";
			
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 4: //buddy ud
						buddyId = tValue.get(c);
						break;
					case 5:// my id that other wants to add
						myId = tValue.get(c);
						break;
				}
				c++;
				this.clientCallback.buddyAuthReq(buddyId, myId);
			}
		}
	}
	
	
	/**
	 * Process buddy list packet
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleBuddyList15(List<String> key, List<String> value)
	{
		/* if we are here the authentication succeded*/
		//create mySelf buddy
		this.me = new AYmsgBuddy(this.getUsername());
		this.buddyList.put(this.me.getId(), this.me);
		
		//start pinging at interval 
		startPingTimer();
		
		try
		{
			this.mService.setState(AYmsgLib.CONN_LOGGED);
		} catch (DeadObjectException e)
		{
			e.printStackTrace();
		}
		this.clientCallback.authSucceded();
		
		int c =0;
		if (key != null)
		{
			AYmsgGroup curG = null;
			for (Iterator<String> i = key.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 302:
						//later check for ignore list
						break;
					case 301: /* This is 319 before all s/n's in a group after the first. It is followed by an identical 300. */
						break;
					case 300: /* This is 318 before a group, 319 before any s/n in a group, and 320 before any ignored s/n. */
						break;
					case 65: /* This is the group */
						AYmsgGroup g = new AYmsgGroup(value.get(c));
						this.groupList.put(g.getId(), g);
						curG = g;
						break;
					case 7: /* this is the Yahoo! Id */
						if (curG != null)
						{
							String id = value.get(c);
							AYmsgBuddy b = new AYmsgBuddy(id, curG);
							curG.addMember(b);	
							this.buddyList.put(id, b);
						} else //add to ignore list
						{
							@SuppressWarnings("unused")
							AYmsgBuddy b = new AYmsgBuddy(value.get(c));
						}
						break;
					case 241:/* another protocol */
						break;
					case 59:/* Cookie ? */
						break;
					case 317:/* Stealth Setting */
						break;												
				}
				c++;
			}
			this.clientCallback.buddyListArrived(this.groupList, this.buddyList);
		}
	}
	
	
	/**
	 * Handle buddy log off
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleBuddyLogOff(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy curBuddy = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 7: //A buddy id
						
						String buddyId = tValue.get(c);
						curBuddy = this.buddyList.get(buddyId); 
						
						break;
					case 10: //Status
						if (curBuddy != null)
						{
							curBuddy.setStatus(Integer.valueOf(tValue.get(c)));
							curBuddy.setOnline(false);
						}
						break;
					case 13: //don't know what for
					case 47: //don't know what for
						break;
				}
				c++;
			}
			//call callback method
			if (curBuddy != null)
				this.clientCallback.buddyLogoff(curBuddy);
			
		}
	}
	
	
	/**
	 * Handle buddy logon
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleBuddyLogOn(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy curBuddy = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 0:// my ID
						break;
					case 7: //A buddy id
						 /* check if user is on BuddyList. create if not found
						  * Some say that when add a new buddy, status update received first
						  * befor ack ?? */
						
						String buddyId = tValue.get(c);
						curBuddy = this.buddyList.get(buddyId); 
						
						break;
					case 10: //Status
						if (curBuddy != null)
						{
							curBuddy.setStatus(Integer.valueOf(tValue.get(c)));
							if (curBuddy.getStatus() != AYmsgType.YAHOO_STATUS_INVISIBLE)
								curBuddy.setOnline(true);
						}
						break;
					case 19: //Custom message
						if (curBuddy != null)
						{
							curBuddy.setCustomStatus(true);
							curBuddy.setStrCustomStatus(tValue.get(c));
						}
						break;
					
				}
				c++;
			}
			//call callback method
			if (curBuddy != null)
				this.clientCallback.buddyLogon(curBuddy);
			
		}
	}

	
	/**
	 * @param key The Key ID
	 * @param value The value
	 * Called when Conference logg off status received.
	 * This is happend when somebody leave the conference room
	 */
	protected void handleConferenceLogOff(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgConference cf = null;
			AYmsgBuddy whoseOl = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 1: //My Id
						break;
					case 56: //whose offline
						String buddyId = tValue.get(c);
						whoseOl = this.buddyList.get(buddyId);						
						if (whoseOl == null)
							whoseOl = new AYmsgBuddy(buddyId);
						break;
					case 57: //the room name
						String id = tValue.get(c);
						cf = this.conferenceList.get(id);
						if (cf == null)
						{
							cf = new AYmsgConference(tValue.get(c));
							this.conferenceList.put(cf.getId(), cf);
						}
						break;
					 default:
						break;
				}
				c++;
			}
			if (cf != null && whoseOl != null)
			{
				//remove buddy from conference
				cf.removeBuddy(whoseOl.getId());
			
				//remove conference from buddy
				whoseOl.removeConference(cf);
				whoseOl = null;
			}
			
			
			//call callbacks
			this.clientCallback.conferenceLoggedOff(whoseOl.getId(),cf);
		}
	}
	
	/**
	 * Called when logged in to Conference
	 * @param key The Key Id
	 * @param value The Value
	 */
	protected void handleConferenceLogOn(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgConference cf = null;
			AYmsgBuddy whoseOl = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 1: //The person 
						break;
					case 53: //whose online
						String buddyId = tValue.get(c);
						whoseOl = this.buddyList.get(buddyId);						
						if (whoseOl == null)
							whoseOl = new AYmsgBuddy(buddyId);
						if (cf != null)
							cf.addBuddy(buddyId, whoseOl);
						break;
					case 57: //the room name
						String id = tValue.get(c);
						cf = this.conferenceList.get(id);
						if (cf == null)
						{
							cf = new AYmsgConference(tValue.get(c));
							this.conferenceList.put(cf.getId(), cf);
						}
						break;
					 default:
						break;
				}
				c++;
			}
			if (cf != null && whoseOl != null)
			{
				//add buddy to conference
				cf.addBuddy(whoseOl.getId(), whoseOl);
			
				//add conference to buddy
				whoseOl.addConference(cf);
			}
			
			
			//call callbacks
			this.clientCallback.conferenceLoggedOn(whoseOl.getId(),cf);
		}
	}
	
	/**
	 * @param key The key 
	 * @param value The value
	 * This Method handle when service is disconnected
	 */
	protected void handleDisconnect(List<String> key, List<String> value)
	{
		int type;
		
		if (this.tPing != null)
		{
			this.tPing.cancel();
		}
		
		//clear resouce
		if (this.groupList != null && this.groupList.size() > 0);
			this.groupList.clear();
		if (this.buddyList != null && this.buddyList.size() > 0);
			this.buddyList.clear();
		
		if (key != null)
		{
			type = Integer.valueOf(value.get(0));
			
			//handle if autoReconnect
			if (this.autoReconnect)
			{
				logout();
				login(this.getUsername(), this.password);
			}
			this.clientCallback.onDisconnected(type);
		}
	}
	
	/**
	 * Handle when Conference invitation received
	 * @param key The Key ID 
	 * @param value The value
	 */
	protected void handleReceivedConferenceInvitation(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgConference cf = null;
			String inviterId = null;
			AYmsgBuddy inviter = null;
			String msg = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 50: //whose inviting
						inviterId = tValue.get(c);
						inviter = this.buddyList.get(inviterId); 
						if (inviter == null)
						{
							inviter = new AYmsgBuddy(inviterId);
							inviter.setOnline(true);
						}
						//update cf member
						if (cf != null)
						{
							//add buddy into conference
							if (cf.getBuddy().get(inviterId) == null)
							{
								cf.addBuddy(inviter.getId(), inviter);
							}
							//add conference ito buddy
							if (inviter.getConference().get(cf.getId()) == null)
							{
								inviter.addConference(cf);
							}
						}
						break;
					case 1: //My id
					case 52: // also my id
						break;
						
					case 53: //user on the conference
						String buddyId = tValue.get(c);
						AYmsgBuddy b = this.buddyList.get(buddyId);
						if (b == null) //not on list
						{
							b = new AYmsgBuddy(buddyId);
							b.setOnline(true);
							
						}
						//add conference into buddy
						if (cf != null)
						{
							//add conference into buddy
							b.addConference(cf);
							
							//add buddy into conference
							cf.addBuddy(b.getId(), b);
						}
						
						break;
					
					case 57: //the room name
					case 234:
						if (cf == null)
						{
							cf = new AYmsgConference(tValue.get(c));
							this.conferenceList.put(cf.getId(), cf);
						}
						break;
					case 58: //the message
						msg = tValue.get(c);
						if (cf != null)
							cf.setMessage(msg);
						break;
					case 13:
					 default:
						break;
				}
				c++;
			}		
			//call callbacks
			this.clientCallback.conferenceReceivedInvitation(cf,inviterId,msg);
		}
	}

	/**
	 * Handle when Conference message received
	 * @param key The Key ID 
	 * @param value The value
	 */
	protected void handleReceivedConferenceMessage(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgConference cf = null;
			String senderId = null;
			AYmsgBuddy sender = null;
			String msg = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 3: //Whoe sending
						senderId = tValue.get(c);
						sender = this.buddyList.get(senderId); 
						if (sender == null)
						{
							sender = new AYmsgBuddy(senderId);
							sender.setOnline(true);
						}
						//update cf member
						if (cf != null)
						{
							//add buddy into conference
							if (cf.getBuddy().get(senderId) == null)
							{
								cf.addBuddy(sender.getId(), sender);
							}
							//add conference ito buddy
							if (sender.getConference().get(cf.getId()) == null)
							{
								sender.addConference(cf);
							}
						}
						
						break;
					case 1: //My id
						break;
																
					case 57: //the room name
						if (cf == null)
						{
							cf = new AYmsgConference(tValue.get(c));
							this.conferenceList.put(cf.getId(), cf);
						}
						break;
					case 14: //the message
						msg = tValue.get(c);
						if (cf != null)
							cf.setMessage(msg);
						break;
					case 97://dont know
					 default:
						break;
				}
				c++;
			}		
			//call callbacks
			this.clientCallback.conferenceReceivedMessage(cf,sender,msg);
		}
	}
	
	/**
	 * Handle when Receive new Messge
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleReceivedMessage(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy buddy = null;
			String msg = "";
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 1:
					case 4: // Friends Id
						String id =  tValue.get(c);
						buddy = this.buddyList.get(id);
						if (buddy == null)
						{
							buddy = new AYmsgBuddy(id);
							//ad to list
							this.buddyList.put(id, buddy);
						}
						break;
					case 5://my id skip
						break;
					case 14: // The Message
						msg = tValue.get(c);
						break;
					case 49: //TYPING
					case 63: // ";0" dont know
					case 64: // "0" dont know
					case 1002: // "1" dont know
					case 10093: // "4" dont know
					case 97:// "1" dont know
					 default:
						break;
				}
				c++;
			}
//			call callbacks
			this.clientCallback.receivedIncomingMessage(buddy,msg);
		}
	}
	
	/**
	 * Handle when Receive (Typing) Notify from buddy
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleReceivedNotify(List<String> key, List<String> value)
	{
		
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy buddy = null;
			boolean on = false;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 4: // Friends Id
						String id =  tValue.get(c);
						buddy = this.buddyList.get(id);
						if (buddy == null)
						{
							buddy = new AYmsgBuddy(id);
							//ad to list
							this.buddyList.put(id, buddy);
						}
						break;
					case 5://my id skip
						break;
					case 13: // Typing On (start) or Off (stop)
						on = (tValue.get(c).equals("1")) ? true: false;
						break;
					case 14:// "" dont know
					case 49: //TYPING
					 default:
						break;
				}
				c++;
			}
//			call callbacks
			this.clientCallback.receivedIncomingNotify(buddy,on);
		}
	}
	
	
	protected void handleReceivedOffMessage(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy buddy = null;
			String msg = "";
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					
					case 1:
					case 4: // Friends Id
						String id =  tValue.get(c);
						buddy = this.buddyList.get(id);
						if (buddy == null)
						{
							buddy = new AYmsgBuddy(id);
							//ad to list
							this.buddyList.put(id, buddy);
						}
						break;
					case 5://my id skip
						break;
					case 14: // The Message
						msg = tValue.get(c);
						break;
					case 31: // "6" dont know
					case 32: // "6" dont know
					case 15: // dont know
					case 97:// "1" dont know
					case 252:// "1" dont know
					case 339:// "1" dont know
					 default:
						break;
				}
				c++;
			}
//			call callbacks
			this.clientCallback.receivedOfflineMessage(buddy,msg);
		}		
	}
	
	/**
	 * Handle when server ACK Remove Buddy
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleRemoveBuddyAck(List<String> key, List<String> value)
	{
		Log.d(this.TAG,"AYmsg.handleRemoveBuddyAck : invoked");
		
		//remove Buddy From a group
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy buddy = null;
			AYmsgGroup group = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 1://my id skip
						break;
					case 60: //don't know what for
						break;
					case 7: // id to remove
						buddy = this.buddyList.get(tValue.get(c));
						break;
					case 65: // buddy's group to remove from
						if (buddy != null)
						{
							String txtGroup = tValue.get(c);
							
						
							//remove Buddy form group 
							group = this.groupList.get(txtGroup);
							group.getBuddyList().remove(buddy.getId());
							
							//remove group from buddy
							buddy.getGroups().remove(txtGroup);
							
							// if this buddy does not contain in any group
							// remove reference
							if (buddy.getGroups().isEmpty())
							{
								this.buddyList.remove(buddy.getId());
							}
							
							//if current group is empty then remove it
							AYmsgGroup tg;
							if ((tg = this.groupList.get(txtGroup)) != null)
							{
								if (tg.getBuddyList().isEmpty())
								{
									this.groupList.remove(txtGroup);
								}
							}
							
						}
						break;
					 default:
						break;
				}
				c++;
			}
			// Call client Callback method
			if (buddy != null && group != null)
				this.clientCallback.buddyRemovedAck(buddy,group);
		}
	}
	
	/**
	 * Process buddy Status Message
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleStatusList15(List<String> key, List<String> value)
	{
		int c =0;
		if (key != null)
		{
			AYmsgBuddy b = null;
			for (Iterator<String> i = key.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 0: /* dont know what for */
						break;
					case 1: /* other use this as point where client are online */
						break;
					case 8: /* how many online buddy we have ?? */
						break;
					case 7: /* buddy name */
						b = this.buddyList.get(value.get(c));
						break;
					case 10: /* state */
						if (b == null)
							break;
						
						b.setOnline(true);
						
						int curVal = Integer.valueOf(value.get(c));
						b.setStatus(curVal);
						
						//Checking for away type
						if ((curVal >= AYmsgType.YAHOO_STATUS_BRB) && (curVal >= AYmsgType.YAHOO_STATUS_STEPPEDOUT))
						{
							b.setAway(true);
						} else
						{
							b.setAway(false);
						}
						
						//Checking for idle type
						if (curVal == AYmsgType.YAHOO_STATUS_IDLE)
						{
							b.setIdle(true);
						}
						else
						{
							b.setIdle(false);
						}
						
						//Checking for custom status
						if (curVal != AYmsgType.YAHOO_STATUS_CUSTOM)
						{
							b.setStrCustomStatus("");
						}
						break;
					case 19: /* custom status message */
						if (b == null)
							break;
						b.setCustomStatus(true);
						b.setStrCustomStatus(value.get(c));
						break;
					case 11: /* buddy session ID */
						break;
					case 17: /* in chat ?? */
						break;
					case 47: /* is custom status away or not ? 2= idle */
						if (b == null)
							break;
						
						//skip if available
						if (b.getStatus() == AYmsgType.YAHOO_STATUS_AVAILABLE)
							break;
						
						if (value.get(c).equals("2"))
						{
							b.setAway(true);
							b.setIdleTime(Integer.valueOf(value.get(c)));
							//maybe later set priciesly in 137
						}
						break;
					case 137: //idle time ?
						if (b == null)
							break;
						if (b.getStatus() != AYmsgType.YAHOO_STATUS_AVAILABLE)
						{
							b.setIdle(true);
							b.setIdleTime(Integer.valueOf(value.get(c)));
						}
						break;
					case 13:
						if (b == null)
							break;
						//update offline (later)
						break;
					case 16: /* custom error message */
						break;
					case 60: /* On SMS */
						if (b == null)
							break;
						b.setOnSMS(true);
						break;
					case 197: /* avatars (later) */
						break;
					case 192: /* buddy icon checksum */
						break;
					case 97: /* use unicode */
						if (b == null)
							break;
						b.setUseUnicode(value.get(c).equals("1"));
						break;
					case 244:
						/* client version detection (later) */
						break;
					
					default:
						Log.d(this.TAG,"handleStatusList15 : got unknown status " + iKey);
				}
				c++;
			}
			this.clientCallback.buddyStatusArrived(this.groupList, this.buddyList);
		}
		
	}
	
	/**
	 * Proces Y6_STATUS_UPDATE. 
	 * this method process buddy status update. update the Buddy on buddyList
	 * and call the method callback
	 * @param key The Key ID
	 * @param value The value
	 */
	protected void handleStatusUpdate(List<String> key, List<String> value)
	{
		List<String> tKey = key;
		List<String> tValue = value;
		
		int c =0;
		if (key != null)
		{
			AYmsgBuddy curBuddy = null;
			for (Iterator<String> i = tKey.iterator(); i.hasNext();)
			{
				String temp = i.next();
				int iKey = Integer.valueOf(temp);
				
				switch (iKey)
				{
					case 7: //A buddy id
						 /* check if user is on BuddyList. create if not found
						  * Some say that when add a new buddy, status update received first
						  * befor ack ?? */
						
						String buddyId = tValue.get(c);
						curBuddy = this.buddyList.get(buddyId); 
						
						if (curBuddy == null)
							curBuddy = new AYmsgBuddy(buddyId);
						break;
					case 10: //Status
						if (curBuddy != null)
						{
							curBuddy.setStatus(Integer.valueOf(tValue.get(c)));
							if (curBuddy.getStatus() != AYmsgType.YAHOO_STATUS_INVISIBLE)
								curBuddy.setOnline(true);
						}
						break;
					case 13: // on Chat & Pager (not handled right now)
						break;
					case 19: //Custom message
						if (curBuddy != null)
						{
							curBuddy.setCustomStatus(true);
							curBuddy.setStrCustomStatus(tValue.get(c));
						}
						break;
					case 47: // 47=1 away 47=2 Idle
						String val = tValue.get(c);
						if (curBuddy != null)
						{
							if (val == "1")
								curBuddy.setAway(true);
							else if (val == "2")
							{
								curBuddy.setIdle(true);
								//set start idle time
							}
						}
						break;
					case 137: //idle time
						if (curBuddy != null)
						{
							curBuddy.setIdleTime(Integer.valueOf(tValue.get(c)));
						}
						break;
					case 138: //reset idle time
						if (curBuddy != null)
						{
							curBuddy.setIdleTime(-1);
						}
						break;
					case 60 ://60=SMS
						if (curBuddy != null)
						{
							curBuddy.setOnSMS(true);
						}
						break;
						// Not implemented YET!
						// 197=Avatars 
						// 192=Friends icon (checksum)
						// ...
						// Add to event object
					
				}
				c++;
			}
			//call callback method
			this.clientCallback.buddyStatusUpdated(curBuddy);
			
		}
	}
}
