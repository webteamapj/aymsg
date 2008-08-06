package org.if_itb.ui;

import java.util.HashMap;
import java.util.List;
import org.if_itb.client.AYmsg;
import org.if_itb.client.AYmsgBuddy;
import org.if_itb.client.AYmsgGroup;
import org.if_itb.client.AYmsgType;
import org.if_itb.client.AYmsgConference;
import org.if_itb.client.R;
import android.os.Handler;
import android.util.Log;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 *
 */
public class ClientCallback implements org.if_itb.client.IAYmsgCallback
{
	
	private final String TAG = "AYmsgClient";


	private ClientMain mainActivity;


	private AYmsg aymsg;
	private Handler handler;
	public ClientCallback(ClientMain a)
	{
		this.mainActivity= a;
		this.handler = new Handler();
	}
	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#authFailed()
	 */
	public void authFailed()
	{
		//just show error message
		
		//allready handle by onDisconeected
		
		//this.onError("Authentication Failed");
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#authSucceded()
	 */
	public void authSucceded()
	{
		Log.i(this.TAG,"ClientCallback.authSucceded : invoked ");
		//Example here set status is Connected
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.setCaption("Authentication Succeded. Retriving Buddy List");
			}
		});
	}
	
	
	public void buddyAddAck(AYmsgBuddy b, AYmsgGroup group)
	{
		Log.i(this.TAG," ClientCallback.buddyAddAck : invoked");
		/*You can update your buddy list. notice that the group is automatically updated
		when buddyAck Received*/
		
//		update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyAuthDeny(java.lang.String, java.lang.String)
	 */
	public void buddyAuthDeny(String buddyId, String myId, String reason)
	{
		Log.i(this.TAG," ClientCallback.buddyAuthDeny : invoked");
		final String buddyIdF = buddyId;
		final String myIdF = myId;
		final String reasonF = reason;
//		 show alert added or not
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doBuddyAuthDeny(buddyIdF, myIdF,reasonF);
			}
		});	
	}


	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyAuthOk(java.lang.String, java.lang.String)
	 */
	public void buddyAuthOk(String buddyId, String myId)
	{
		Log.i(this.TAG," ClientCallback.buddyAuthOk : invoked");
		final String buddyIdF = buddyId;
		final String myIdF = myId;
		
		// show alert added or not
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doBuddyAuthOk(buddyIdF, myIdF);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyAuthReq(java.lang.String, java.lang.String)
	 */
	public void buddyAuthReq(String buddyId, String myId)
	{
		Log.i(this.TAG," ClientCallback.buddyAuthReq : invoked");
		final String buddyIdF = buddyId;
		final String myIdF = myId;
		
		// show alert added or not
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doShowAuthReq(buddyIdF, myIdF);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyListArrived(java.util.ArrayList, java.util.HashMap)
	 */
	public void buddyListArrived(HashMap<String,AYmsgGroup> groupList, HashMap<String, AYmsgBuddy> buddyList)
	{
		Log.i(this.TAG,"ClientCallback.buddyListArrived : invoked ");
		
		/* This is Example to traversal and print buddy list */
		/* at this point, list is retrieved maybe not complete */
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.setCustomStatus(AYmsgType.YAHOO_STATUS_AVAILABLE, ClientCallback.this.mainActivity.getString(R.string.default_status));
			}
		});
		
		/*if (groupList != null)
		{
			for (Iterator<AYmsgGroup> i = groupList.iterator(); i.hasNext();)
			{
				AYmsgGroup temp = i.next();
				Log.i(TAG,"Buddy list : " + temp.getId());
				 
				//iterate trough all buddy
				ArrayList<AYmsgBuddy> bList = temp.getBuddyList();
				if (bList != null)
				{
					for (Iterator<AYmsgBuddy> curBuddy = bList.iterator(); curBuddy.hasNext();)
					{
						AYmsgBuddy tCur = curBuddy.next();
						Log.i(TAG,"Yahoo! Id : " + tCur.getId());
					}
				}
				
			}
		}*/	
		
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyLogoff(org.if_itb.aymsg.AYmsgBuddy)
	 */
	public void buddyLogoff(AYmsgBuddy curBuddy)
	{
		Log.i(this.TAG,"ClientCallback.buddyLogoff : invoked ");
		//update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});
	}

	
	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyLogon(org.if_itb.aymsg.AYmsgBuddy)
	 */
	public void buddyLogon(AYmsgBuddy curBuddy)
	{
		Log.i(this.TAG,"ClientCallback.buddyLogon : invoked ");
		// update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyRemoved(org.if_itb.aymsg.AYmsgBuddy, org.if_itb.aymsg.AYmsgGroup)
	 */
	public void buddyRemovedAck(AYmsgBuddy buddy, AYmsgGroup group)
	{
		Log.i(this.TAG," ClientCallback.buddyRemovedAck : invoked");
		/*You can update your buddy list. notice that the group is automatically updated
		when buddyRevoceAck Received*/
		
		//update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyStatusArrived(java.util.ArrayList, java.util.HashMap)
	 */
	public void buddyStatusArrived(HashMap<String,AYmsgGroup> groupList, HashMap<String, AYmsgBuddy> buddyList)
	{
		Log.i(this.TAG,"ClientCallback.buddyStatusArrived : invoked ");
		
		/* called when buddy list completed */
		/* and packet status is received */
		
		//update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});
		
	}


	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#buddyStatusUpdated(org.if_itb.aymsg.AYmsgBuddy)
	 */
	public void buddyStatusUpdated(AYmsgBuddy curBuddy)
	{
		Log.i(this.TAG," ClientCallback.buddyStatusUpdated : invoked");
		//update buddy list in main view
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateList();
			}
		});	
	}
	
	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#conferenceLoggedOff(org.if_itb.aymsg.AymsgConference)
	 */
	public void conferenceLoggedOff(String who, AYmsgConference cf)
	{
		Log.i(this.TAG,"ClientCallback.conferenceLoggedOff > invoked for user:" + who + " room: " + cf.getId());
		final AYmsgConference cfF = cf;
		this.handler.post(new Runnable() {
			public void run()
			{
				ClientCallback.this.mainActivity.doUpdateConference(cfF);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#conferenceLoggedOn(org.if_itb.aymsg.AymsgConference)
	 */
	public void conferenceLoggedOn(String who, AYmsgConference cf)
	{
		Log.i(this.TAG,"ClientCallback.conferenceLoggedOn > invoked for user: " + who + " room: " + cf.getId());
		final AYmsgConference cfF = cf;
		this.handler.post(new Runnable() {
			public void run()
			{
				ClientCallback.this.mainActivity.doUpdateConference(cfF);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#conferenceReceivedInvitation(org.if_itb.aymsg.AymsgConference, java.lang.String, java.lang.String)
	 */
	public void conferenceReceivedInvitation(AYmsgConference cf, String inviter, String msg)
	{
		Log.i(this.TAG," ClientCallback.conferenceReceivedInvitation : room" + cf.getId() + " message : " + msg + "members :" + cf.getBuddy().size());
		
		String names[] = new String[cf.getBuddy().size()];
		cf.getBuddy().keySet().toArray(names);
		for (String s : names)
		{
			Log.d(this.TAG,"name: " + s);
		}
		
		final AYmsgConference cfF = cf;
		final String inviterF = inviter;
		final String msgF = msg;
		this.handler.post(new Runnable() {
			public void run()
			{
				ClientCallback.this.mainActivity.doConferenceInvited(cfF, inviterF, msgF);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#conferenceReceivedMessage(org.if_itb.aymsg.AymsgConference, org.if_itb.aymsg.AYmsgBuddy, java.lang.String)
	 */
	public void conferenceReceivedMessage(AYmsgConference cf, AYmsgBuddy sender, String msg)
	{
		final AYmsgConference cfF = cf;
		final AYmsgBuddy senderF = sender;
		final String msgF = msg;
		this.handler.post(new Runnable() {
			public void run()
			{
				ClientCallback.this.mainActivity.doUpdateMessageConference(cfF, senderF, msgF);
			}
		});	
	}

	// Custom function
	public AYmsg getAymsg()
	{
		return this.aymsg;
	}

	public void onAYmsgReady()
	{
		Log.i(this.TAG,"ClientCallback.onAYmsgReady : invoked ");
		//Service is ready, begin login process
		this.mainActivity.doLogin();
		
	}

	/* (non-Javadoc)
	 * @see org.if_itb.client.IAYmsgCallback#onDisconnected(int)
	 */
	public void onDisconnected(int type)
	{		
		Log.i(this.TAG, "ClientCallback.onDisconnected >> type : " + type);
		
		final int fType = type;
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.onDisconnected(fType);
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#onError(java.lang.String)
	 */
	public void onError(String message)
	{

		final String messagef = message; 
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doError(messagef);
			}
		});

	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.AYmsgCallback#rawPacketHandler(java.util.List, java.util.List, int, int, int)
	 */
	public boolean rawPacketHandler(List<String> key, List<String> value, int Service, int Status, int SessionId)
	{
		boolean handled = false;
		
		Log.d(this.TAG,String.format("AYmsgClientCallback.rawPacketHandler Service:%d Status:%d SessionId:%d",Service, Status, SessionId));

		/*ListIterator<String> itmKey = key.listIterator();
		ListIterator<String> itmVal = value.listIterator();*/
		/*for (;itmKey.hasNext();)
		{
			Log.d(TAG,"key=" + itmKey.next() + " value=" +  itmVal.next());
		}*/
		return handled;
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#receivedIncomingMessage(org.if_itb.aymsg.AYmsgBuddy, java.lang.String)
	 */
	public void receivedIncomingMessage(AYmsgBuddy buddy, String msg)
	{
		Log.i(this.TAG," ClientCallback.receivedIncomingMessage : from " + buddy.getId() + " message " + msg);
		final AYmsgBuddy fBuddy = buddy;
		final String fMsg = msg;
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateMessage(fBuddy, fMsg);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.aymsg.IAYmsgCallback#receivedNotify(org.if_itb.aymsg.AYmsgBuddy)
	 */
	public void receivedIncomingNotify(AYmsgBuddy buddy, boolean on)
	{
		Log.i(this.TAG," ClientCallback.receivedIncomingNotify : from " + buddy.getId() + " type " + on);
		// show notify icon
		final AYmsgBuddy fBuddy = buddy;
		final boolean fOn = on;
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateNotify(fBuddy, fOn);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see org.if_itb.client.IAYmsgCallback#receivedOfflineMessage(org.if_itb.client.AYmsgBuddy, java.lang.String)
	 */
	public void receivedOfflineMessage(AYmsgBuddy buddy, String msg)
	{
		Log.i(this.TAG," ClientCallback.receivedOfflineMessage : from " + buddy.getId() + " message " + msg);
		final AYmsgBuddy fBuddy = buddy;
		final String fMsg = msg;
		this.handler.post(new Runnable() {
			public void run() {
				ClientCallback.this.mainActivity.doUpdateMessage(fBuddy, fMsg);
			}
		});	
	}
	
	
	public void setAymsg(AYmsg aymsg)
	{
		this.aymsg = aymsg;
	}
}
