package org.if_itb.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.if_itb.client.AYmsg;
import org.if_itb.client.AYmsgBuddy;
import org.if_itb.client.AYmsgConference;
import org.if_itb.client.AYmsgGroup;
import org.if_itb.client.AYmsgType;
import org.if_itb.client.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost.TabContentFactory;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * 
 */
public class ClientMain extends ListActivity
{
	public final static String TAG = "AYmsgClient";
	public static final int SET_STATUS = 1;
	public static final int ADD_BUDDY = 2;
	public static final int CLOSE_TAB = 3;
	public static final int CREATE_CONF = 4;
	private final String BUDDY_LIST = "Buddy List";
	private String username = "";
	private String password = "";
	private HashMap<String, TabHost.TabSpec> tabList = new HashMap<String, TabHost.TabSpec>();
	private HashMap<String, TabHost.TabSpec> confTabList = new HashMap<String, TabHost.TabSpec>();
	private HashMap<String, AnimatedImageView> typingList = new HashMap<String, AnimatedImageView>();
	private HashMap<String, LogWebView> chatList = new HashMap<String, LogWebView>();
	private HashMap<String, LogWebView> confTextList = new HashMap<String, LogWebView>();
	private HashMap<String, LogTextBox> confTextMember = new HashMap<String, LogTextBox>();
	AYmsg aymsg;
	ClientCallback clientCallback = new ClientCallback(this);
	TabHost.TabSpec main;
	TabHost th = null;

	/**
	 * @param confId
	 * Exit from Conference
	 */
	private void doExitConference(String confId)
	{
		if (this.aymsg == null)
			return;
		try
		{
			this.aymsg.conferenceLogOff(this.aymsg.getUsername(), this.aymsg.getConferenceList().get(confId));
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param room the room name
	 * Create conference room 
	 */
	public void createConference (String room)
	{
		Intent i = new Intent(ClientMain.this, ClientCreateConf.class);
		this.startSubActivity(i, CREATE_CONF);
	}

	public void doBuddyAuthDeny(String buddyId, String myId, String reason)
	{
		Toast.makeText(this, buddyId + "Rejected to add " + myId + " because \"" + reason + "\"",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param buddyIdF
	 * @param myIdF
	 * Called when auth ok
	 */
	public void doBuddyAuthOk(String buddyId, String myId)
	{
		Toast.makeText(this, "Sucessfulyy added " + buddyId,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param cf
	 *            The conference Object
	 * @param inviter
	 *            the inviter's Yahoo! id
	 * @param message
	 *            the invitation Message Called when Conference invitation
	 *            received
	 */
	public void doConferenceInvited(AYmsgConference cf, String inviter,
			String message)
	{
		// show Invitation dialog
		final AYmsgConference cfF = cf;
		new AlertDialog.Builder(ClientMain.this)
				.setIcon(R.drawable.chat)
				.setTitle(
						"Conference Invitation: \nFrom: " + inviter + "\nRoom:" + cf.getId() + "\n\"" + message
								+ "\"\n\n Would you like to join this conference ?")
				.setNegativeButton(R.string.btn_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
							/* Deny conference */
							}
						})
						.setPositiveButton(R.string.btn_join,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								try
								{
									String id = cfF.getId();
									
									//update user in conference
									
									int size = cfF.getBuddy().size()+1;
									String names[] = new String[size];
									cfF.getBuddy().keySet().toArray(names);
									names[size-1] = ClientMain.this.aymsg.getUsername();
									
									//check for exsisting tab;
									TabHost.TabSpec cfTab = ClientMain.this.confTabList.get(id); 
									if (cfTab == null) //not exists create new
									{
										cfTab = ClientMain.this.newConferenceTab(id,names);
										ClientMain.this.th.addTab(cfTab);
										ClientMain.this.confTabList.put(id, cfTab);			
									} else //update user in coference
									{
										for (String name : names)
										{
											AYmsgBuddy b = ClientMain.this.aymsg.getBuddyList().get(name);
											if (b == null) //not exists
											{
												b = new AYmsgBuddy(name);
											}
											//add conferecnce to buddy
											b.addConference(cfF);
											//add buddy to Converence
											cfF.addBuddy(b.getId(), b);
										}
									}
									
									
									ClientMain.this.aymsg.conferenceJoinInvitation(cfF);
								} catch (IllegalStateException e)
								{
									e.printStackTrace();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}).show();
	}

	/**
	 * @param message the error message
	 * Called when error happened
	 */
	public void doError(String message)
	{
		Toast.makeText(this, message,
				Toast.LENGTH_SHORT).show();		
	}

	/**
	 * do the actual login, will be called when service is ready
	 */
	public void doLogin()
	{
		this.setCaption("Loggin in..");
		this.aymsg.login(this.username, this.password);
	}

	/**
	 * @param buddyIdF
	 * @param myIdF
	 * Show alert to allow buddy authentication
	 */
	public void doShowAuthReq(String buddyId, String myId)
	{
//		 show Invitation dialog
		if (buddyId == null || buddyId.length() == 0 || myId == null || myId.length() == 0 )
			return;
		
		final String buddyIdF= buddyId;
		final String myIdF = myId;
		
		new AlertDialog.Builder(ClientMain.this)
				.setIcon(R.drawable.chat)
				.setTitle(
						buddyIdF +" wants to add " + myIdF + " as a friend."
								+ "\n\n Would you like to allow it ?")
				.setNegativeButton(R.string.btn_deny,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								try
								{
									ClientMain.this.aymsg.denyBuddy(buddyIdF, myIdF, "Thanks, but no thanks..");
								} catch (IllegalArgumentException e)
								{
									e.printStackTrace();
								} catch (IllegalStateException e)
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
						})
						.setPositiveButton(R.string.btn_allow,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								try
								{
									ClientMain.this.aymsg.authBuddy(buddyIdF, myIdF);
								} catch (IllegalArgumentException e)
								{
									e.printStackTrace();
								} catch (IllegalStateException e)
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
						}).show();
	}

	/**
	 * Called when Conference logged in
	 */
	public void doUpdateConference(AYmsgConference cf)
	{
		AYmsgConference cfTemp = cf;
		String id = cfTemp.getId();
		
		//check for exsisting tab;
		TabHost.TabSpec cfTab = this.confTabList.get(id); 
		if (cfTab == null) //not exists create new
		{
			cfTab = this.newConferenceTab(id);
			this.th.addTab(cfTab);
			this.confTabList.put(id, cfTab);			
		}
		
		// update chat member;
		LogTextBox ltm = this.confTextMember.get(id);
		if (ltm != null)
		{
			//genereate string of buddy member
			StringBuffer sb = new StringBuffer();

			if (cfTemp.getBuddy().size() > 0)
			{
				String arrBudy[] = new String[cfTemp.getBuddy().size()];
				cfTemp.getBuddy().keySet().toArray(arrBudy);

				for (String key : arrBudy)
				{
					sb.append(key + "\n");
				}
				ltm.setText(sb.toString());
			}
		}
		
		
	}
	
	/**
	 * Update the buddy list. Create IconifiedText and reset the ListAdapter
	 * 
	 * @param groupList
	 */
	public void doUpdateList()
	{
		HashMap<String, AYmsgGroup> groupList = this.aymsg.getGroupList();
		Log.i(TAG, "doUpdateList : invoked");
		if (groupList != null)
		{
			HashMap<String, AYmsgGroup> tGroupList = groupList;
			IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
			String groupNames[] = new String[groupList.size()];
			groupList.keySet().toArray(groupNames);
			Arrays.sort(groupNames, String.CASE_INSENSITIVE_ORDER);
			for (Iterator<String> i = tGroupList.keySet().iterator(); i
					.hasNext();)
			{
				AYmsgGroup tempGroup = tGroupList.get(i.next());
				// Create new Iconified Text for group
				IconifiedText curGroup = new IconifiedText(tempGroup.getId(),
						this.getResources().getDrawable(R.drawable.group));
				curGroup.setType(IconifiedText.TYPE_GROUP);
				curGroup.setGroup(tempGroup);
				itla.addItem(curGroup);
				// iterate trough all buddy
				HashMap<String, AYmsgBuddy> bList = tempGroup.getBuddyList();
				if (bList != null)
				{
					String buddyName[] = new String[bList.size()];
					bList.keySet().toArray(buddyName);
					Arrays.sort(buddyName, String.CASE_INSENSITIVE_ORDER);
					for (String curBuddy : buddyName)
					{
						AYmsgBuddy tCur = bList.get(curBuddy);
						String displayName = tCur.getId();
						if (tCur.isCustomStatus())
							displayName += " (" + tCur.getStrCustomStatus()
									+ ")";
						// check for online
						Drawable d;
						if (tCur.isOnline())
							d = this.getResources().getDrawable(R.drawable.user_on);
						else
							d = this.getResources().getDrawable(R.drawable.user_off);
						IconifiedText curB = new IconifiedText(displayName, d,
								20);
						curB.setType(IconifiedText.TYPE_BUDDY);
						curB.setBuddy(tCur);
						curB.setGroup(tempGroup);
						itla.addItem(curB);
					}
				}
			}
			ListView lv = (ListView)findViewById(android.R.id.list);
			int y = lv.getScrollY(); 
			int x = lv.getScrollX(); 
			this.setListAdapter(itla);
			lv.scrollTo(x, y);
			
		}
	}
	
	/**
	 * Update the Message Tab, if there is no tab, then create one and select it
	 * 
	 * @param buddy
	 * @param msg
	 */
	public void doUpdateMessage(AYmsgBuddy buddy, String msg)
	{
		LogWebView ltb;
		String id = buddy.getId();
		ltb = this.chatList.get(id);
		if (ltb == null) // tab Not Exist create one
		{
			if (this.th != null)
			{
				msg = "<font color=\"#ff0000\">" + id + ": </font>" + msg + "<br>";;
				TabHost.TabSpec newTab = this.newChatTab(id,msg);
				this.tabList.put(id, newTab);
				this.th.addTab(newTab);
				
				Log.d(TAG, "doUpdateMessage : create new tab. chatlis size : "
						+ this.chatList.size());
			}
		}
		else 
		{
			Log.d(TAG, "doUpdateMessage : ltb isnot null");
			if (msg.equals("<ding>")) // buzz
				ltb.append("<font color=\"#00ff00\">" +id + " just buzzing you !" + "</font><br>");
			else
			{
				String msgC ="";
				if (msg.charAt(0) == 0x1b)
				{
					msgC = msg.substring(msg.indexOf(">")+1);
				}
				else
				{
					msgC = msg;
				}
				
				ltb.append("<font color=\"#ff0000\">" + id + ": </font>" + msgC + "<br>");
			}
			this.doUpdateNotify(buddy, false); // set off the typing icon
		}
	}

	public void doUpdateMessageConference(AYmsgConference cfF, AYmsgBuddy senderF, String msgF)
	{
		//if me, just skip
		if (senderF != null && senderF.getId() == this.aymsg.getUsername())
			return;
		
		if (cfF != null)
		{
			AYmsgConference cf = cfF; //for efficiency
			LogWebView cft = this.confTextList.get(cf.getId()) ; 
			if (cft != null)
			{
				String msgC ="";
				if (msgF.charAt(0) == 0x1b)
				{
					msgC = msgF.substring(msgF.indexOf(">")+1);
				}
				else
				{
					msgC = msgF;
				}

				cft.append("<font color=\"#ff0000\">" + senderF.getId() + ": </font>" + msgC + "<br>");
			}
		}
		
	}

	/**
	 * Update the typing animation
	 * 
	 * @param buddy
	 *            the boddy to update
	 * @param on
	 *            if true then set it visible else setin invisible
	 */
	public void doUpdateNotify(AYmsgBuddy buddy, boolean on)
	{
		String id = buddy.getId();
		AnimatedImageView aiv;
		Log.d(TAG, "doUpdateNotify : from " + buddy + " type " + on);
		@SuppressWarnings("unused")
		TabHost.TabSpec ltb;
		if ((ltb = this.tabList.get(id)) == null) // tab Not Exist create one
		{
			if (this.th != null)
			{
				TabHost.TabSpec newTab = this.newChatTab(id);
				this.tabList.put(id, newTab);
				this.th.addTab(newTab);
			}
		}
		if ((aiv = this.typingList.get(id)) != null)
		{
			if (on)
			{
				aiv.start();
			} else
			{
				aiv.stop();
			}
		} else
		{}
	}

	/**
	 * Function for created new Tab
	 * 
	 * @param tag
	 *            Tab name
	 * @return new Tab host
	 */
	public TabHost.TabSpec newChatTab(String tag)
	{
		return newChatTab(tag, "");
	}
	
	/**
	 * Function for created new Tab
	 * 
	 * @param tag
	 *            Tab name
	 * @return new Tab host
	 */
	public TabHost.TabSpec newChatTab(String tag, String message)
	{
		final String buddyId = tag;
		final String fMsg = message;
		if (this.th != null)
		{
			TabHost.TabSpec newTab = this.th.newTabSpec(tag);
			newTab.setContent(new TabContentFactory() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
				 */
				public View createTabContent(String arg0)
				{
					MyTab rl = new MyTab(ClientMain.this, buddyId);
					rl
							.setLayoutParams(new LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.FILL_PARENT));
					rl.setPadding(10, 10, 10, 10);
					// Put typing animation
					int anim[] = { R.drawable.typing0, R.drawable.typing1,
							R.drawable.typing2, R.drawable.typing3,
							R.drawable.typing4 };
					AnimatedImageView typing = new AnimatedImageView(
							ClientMain.this, anim);
					typing.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					typing.stop();
					ClientMain.this.typingList.put(buddyId, typing);
					typing.setPadding(0, 10, 6, 6);
					rl.addView(typing);
					typing.setId(6);
					// put buddy icon
					ImageView iv = new ImageView(ClientMain.this);
					iv.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					iv.setImageResource(R.drawable.user_on);
					iv.setPadding(0, 0, 6, 6);
					iv.setId(1);
					RelativeLayout.LayoutParams rlLayout = new LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 6);
					rl.addView(iv, rlLayout);
					// put label buddy
					TextView tx = new TextView(ClientMain.this);
					tx
							.setLayoutParams(new LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					tx.setText(buddyId);
					tx.setPadding(0, 5, 0, 0);
					tx.setId(2);
					tx.setTypeface(Typeface.DEFAULT_BOLD);
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 1);
					rl.addView(tx, rlLayout);
					
					// put LogTextBox
					/*LogTextBox ltb = new LogTextBox(ClientMain.this);
					ltb.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, 270));
					ltb.setBackgroundColor(Color.WHITE);
					ltb.setTextColor(Color.BLACK);
					//ltb.setBackground(android.R.drawable.box);
					ltb.setId(3);
					ClientMain.this.chatList.put(buddyId, ltb);*/
					
					Log.d(TAG, "putting chat list : " + ClientMain.this.chatList.size());
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 260);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 1);
					rlLayout.setMargins(0, 0, 0, 6);
					//rl.addView(ltb, rlLayout);
					
					LogWebView wv = new LogWebView(ClientMain.this);
					wv.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT, 270));				
			
					wv.setId(3);
					wv.append(fMsg);
					ClientMain.this.chatList.put(buddyId, wv);
					rl.addView(wv, rlLayout);
					
					
					// put input message box
					EditText et = new EditText(ClientMain.this);
					et.setId(4);
					rlLayout = new LayoutParams(240, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 3);
					rl.addView(et, rlLayout);
					final LogWebView ltbf = wv;
					final EditText etf = et;
					et.setOnKeyListener(new OnKeyListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see android.view.View.OnKeyListener#onKey(android.view.View,
						 *      int, android.view.KeyEvent)
						 */
						public boolean onKey(View arg0, int arg1, KeyEvent arg2)
						{
							if (etf.getText().length() > 0)
							{
								String text = etf.getText().toString();
								if (arg1 == KeyEvent.KEYCODE_NEWLINE)
								{
									if (ClientMain.this.aymsg != null)
									{
										etf.setText("");
										ltbf.append("<font color=\"#0000ff\">" +ClientMain.this.aymsg.getUsername() + ": </font>"
												+ text + "<br>");
										try
										{
											ClientMain.this.aymsg.sendMessage(ClientMain.this.aymsg
													.getUsername(), buddyId,
													text);
										} catch (IllegalStateException e)
										{
											e.printStackTrace();
										} catch (IOException e)
										{
											e.printStackTrace();
										}
										return true;
									}
								} else if (etf.getText().length() == 1
										&& arg1 != KeyEvent.KEYCODE_DEL) // send
																			// notify
																			// typing
								{
									try
									{
										if (ClientMain.this.aymsg != null)
											ClientMain.this.aymsg.sendNotify(ClientMain.this.aymsg
													.getUsername(), buddyId,
													true, "");
										return false;
									} catch (IllegalStateException e)
									{
										e.printStackTrace();
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								} else if (etf.getText().length() == 1
										&& arg1 == KeyEvent.KEYCODE_DEL)
								{
									Log.d(TAG, "clear detected DEl key = "
											+ arg1);
									try
									{
										ClientMain.this.aymsg.sendNotify(ClientMain.this.aymsg.getUsername(),
												buddyId, false, "");
									} catch (IllegalStateException e)
									{
										e.printStackTrace();
									} catch (IOException e)
									{
										e.printStackTrace();
									}
									return false;
								}
								return false;
							}
							return false;
						}
					});
					// put send button
					Button bt = new Button(ClientMain.this);
					bt.setText(org.if_itb.client.R.string.btn_send);
					bt.setOnClickListener(new OnClickListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see android.view.View.OnClickListener#onClick(android.view.View)
						 */
						public void onClick(View arg0)
						{
							if (etf.getText().length() > 0)
								if (ClientMain.this.aymsg != null)
								{
									if (ClientMain.this.aymsg != null)
									{
										String text = etf.getText().toString();
										etf.setText("");
										ltbf.append("<font color=\"#0000ff\">" + ClientMain.this.aymsg.getUsername() + ": </font>"
												+ text + "<br>");
										try
										{
											AnimatedImageView aiv;
											if ((aiv = ClientMain.this.typingList.get(buddyId)) != null)
											{
												aiv.stop();
											}
											ClientMain.this.aymsg.sendMessage(ClientMain.this.aymsg
													.getUsername(), buddyId,
													text);
										} catch (IllegalStateException e)
										{
											e.printStackTrace();
										} catch (IOException e)
										{
											e.printStackTrace();
										}
									}
								}
						}
					});
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 3);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 4);
					rl.addView(bt, rlLayout);
					return rl;
				}
			});
			newTab.setIndicator(tag, this.getResources().getDrawable(
					R.drawable.buddy));
			return newTab;
		}
		return null;
	}

	public TabHost.TabSpec newConferenceTab(String tag)
	{
		return this.newConferenceTab(tag, null);
	}

	public TabHost.TabSpec newConferenceTab(String tag,String[] users)
	{
		final String ConfId = tag;
		final String usersF[] = users;
		if (this.th != null)
		{
			TabHost.TabSpec newTab = this.th.newTabSpec(tag);
			newTab.setContent(new TabContentFactory() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
				 */
				public View createTabContent(String arg0)
				{
					MyTab rl = new MyTab(ClientMain.this, ConfId);
					rl
					.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.FILL_PARENT));
					rl.setPadding(10, 10, 10, 10);
					//rl.setBackgroundColor(android.graphics.Color.WHITE);
					
					// put buddy icon
					ImageView iv = new ImageView(ClientMain.this);
					iv.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					iv.setImageResource(R.drawable.user_on);
					iv.setPadding(0, 0, 6, 6);
					iv.setId(1);
					RelativeLayout.LayoutParams rlLayout = new LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rl.addView(iv, rlLayout);
					
					// put label Conference
					TextView tx = new TextView(ClientMain.this);
					tx.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					tx.setText(ConfId);
					tx.setPadding(0, 5, 0, 0);
					tx.setId(2);
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 1);
					rl.addView(tx, rlLayout);
					
					//put Exit Conference
					Button btExit = new Button(ClientMain.this);
					btExit.setText(org.if_itb.client.R.string.btn_exit);
					btExit.setOnClickListener(new OnClickListener(){
						
						/* (non-Javadoc)
						 * @see android.view.View.OnClickListener#onClick(android.view.View)
						 */
						public void onClick(View arg0)
						{
							ClientMain.this.doExitConference(ConfId);
						}
					});
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							35);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 2);
					rl.addView(btExit, rlLayout);
					
					// put LogTextBox
					/*LogTextBox ltb = new LogTextBox(ClientMain.this);
					ltb.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, 270));
					
					//ltb.setBackground(android.R.drawable.box);
					ltb.setTextColor(Color.BLACK);
					//ltb.setId(3);*/
					
					rlLayout = new LayoutParams(200, 260);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 1);
					rlLayout.setMargins(0, 0, 0, 6);
					//rl.addView(ltb, rlLayout);
					
					LogWebView wv = new LogWebView(ClientMain.this);
					wv.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT, 270));				
			
					wv.setId(3);
					rl.addView(wv, rlLayout);
					ClientMain.this.confTextList.put(ConfId, wv);
					
					
					//put confMember 
					LogTextBox lcm = new LogTextBox(ClientMain.this);					
					lcm.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT, 270));
					//lcm.setBackground(android.R.drawable.box);
					lcm.setBackgroundColor(android.graphics.Color.WHITE);
					lcm.setTextColor(Color.BLACK);
					lcm.setId(7);
					//put Buddy member of Conference
					if (usersF != null)
						for (String user: usersF)
						{
							lcm.append(user + "\n");
						}
					
					ClientMain.this.confTextMember.put(ConfId, lcm);
					
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 260);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 3);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 1);
					rlLayout.setMargins(6, 0, 0, 6);
					rl.addView(lcm, rlLayout);
					
					// put input message box
					EditText et = new EditText(ClientMain.this);
					et.setId(4);
					rlLayout = new LayoutParams(240, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 3);
					rl.addView(et, rlLayout);
					final LogWebView lwvf = wv;
					final EditText etf = et;
					et.setOnKeyListener(new OnKeyListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see android.view.View.OnKeyListener#onKey(android.view.View,
						 *      int, android.view.KeyEvent)
						 */
						public boolean onKey(View arg0, int arg1, KeyEvent arg2)
						{
							if (etf.getText().length() > 0)
							{
								String text = etf.getText().toString();
								if (arg1 == KeyEvent.KEYCODE_NEWLINE)
								{
									if (ClientMain.this.aymsg != null)
									{
										etf.setText("");
										lwvf.append("<font color=\"#0000ff\">" + ClientMain.this.aymsg.getUsername() + ":</font> "
												+ text + "<br>");
										try
										{
											ClientMain.this.aymsg.conferenceSendMessage(ClientMain.this.aymsg.getConferenceList().get(ConfId), text);
										} catch (IllegalStateException e)
										{
											e.printStackTrace();
										} catch (IOException e)
										{
											e.printStackTrace();
										}
										return true;
									}
								} 
								return false;
							}
							return false;
						}
					});
					// put send button
					Button bt = new Button(ClientMain.this);
					bt.setText(org.if_itb.client.R.string.btn_send);
					bt.setOnClickListener(new OnClickListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see android.view.View.OnClickListener#onClick(android.view.View)
						 */
						public void onClick(View arg0)
						{
							if (etf.getText().length() > 0)
								if (ClientMain.this.aymsg != null)
								{
									if (ClientMain.this.aymsg != null)
									{
										String text = etf.getText().toString();
										etf.setText("");
										lwvf.append("<font color=\"#0000ff\">" + ClientMain.this.aymsg.getUsername() + ": </font>"
												+ text + "<br>");
										try
										{
											ClientMain.this.aymsg.conferenceSendMessage(ClientMain.this.aymsg.getConferenceList().get(ConfId), text);
										} catch (IllegalStateException e)
										{
											e.printStackTrace();
										} catch (IOException e)
										{
											e.printStackTrace();
										}
									}
								}
						}
					});
					rlLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					rlLayout.addRule(RelativeLayout.POSITION_BELOW, 3);
					rlLayout.addRule(RelativeLayout.POSITION_TO_RIGHT, 4);
					rl.addView(bt, rlLayout);
					return rl;
				}
			});
			newTab.setIndicator(tag, this.getResources().getDrawable(
					R.drawable.chat));
			return newTab;
		}
		return null;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle)
	{
		Log.d(TAG, "ClientMain.onCreate");
		super.onCreate(icicle);
		this.setContentView(R.layout.main);
		// set list adapter
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		this.setListAdapter(itla);
		this.th = (TabHost) this.findViewById(R.id.main_tabhost);
		this.th.setup();
		this.main = this.th.newTabSpec(this.BUDDY_LIST);
		this.main.setContent(R.id.mainTab);
		this.main.setIndicator(this.BUDDY_LIST, this.getResources().getDrawable(
				R.drawable.group));
		this.th.addTab(this.main);
		this.th.setCurrentTabByTag(this.BUDDY_LIST);
		
		// test
		 //th.addTab(newConferenceTab("Test"));
		//th.addTab(newChatTab("Test"));
		
		Bundle b = this.getIntent().getExtras();
		if (b == null)
			return;
		this.username = b.getString("user");
		this.password = b.getString("pass");
		this.aymsg = new AYmsg(this);
		this.setCaption("Starting Service..");
		this.aymsg.startService();
		// pass reference aymsg so callback can call function
		this.clientCallback.setAymsg(this.aymsg);
		// set function callback
		this.aymsg.registerClientCallback(this.clientCallback);
		// when service is started, it will automaticly call onAYmsgReady
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SET_STATUS, R.string.menu_change_status);
		menu.add(0, ADD_BUDDY, R.string.menu_add_buddy);
		// menu.add(0, CLOSE_TAB, R.string.menu_close_tab);
		return result;
	}

	public void onDisconnected(int type)
	{
		String message = "";
		
		if (type == AYmsgType.DC_AUTH_FAILED)
			message = getString(R.string.dc_auth_failed);
		else if (type == AYmsgType.DC_CON_ERROR)
			message = getString(R.string.dc_error);
		else if (type == AYmsgType.DC_OTHER_LOGED)
			message = getString(R.string.dc_other_log);
		
		//show alert
		new AlertDialog.Builder(ClientMain.this)
		.setTitle(getString(R.string.disconnected))
		.setMessage(message)
		.setPositiveButton("OK", null)
		.show();
		
	
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.Menu.Item)
	 */
	@Override
	public boolean onOptionsItemSelected(Item item)
	{
		switch (item.getId())
		{
			case SET_STATUS:
				Intent i = new Intent(ClientMain.this, ClientStatus.class);
				this.startSubActivity(i, SET_STATUS);
				break;
			case ADD_BUDDY:
				Intent i2 = new Intent(ClientMain.this, ClientBuddyAdd.class);
				HashMap<String, AYmsgGroup> gl = this.aymsg.getGroupList();
				Bundle glist = new Bundle();
				if (!gl.isEmpty())
				{
					for (Iterator<String> it = gl.keySet().iterator(); it
							.hasNext();)
					{
						AYmsgGroup tmp = gl.get(it.next());
						glist.putString(tmp.getId(), tmp.getId());
					}
				}
				i2.putExtra(ClientBuddyAdd.GROUP_LIST, glist);
				this.startSubActivity(i2, ADD_BUDDY);
				break;
			case CLOSE_TAB: /* not working */
				if (this.th != null)
				{
					String tag = this.th.getCurrentTabTag();
					if (!tag.equals(this.BUDDY_LIST))
					{
						// remove from tab list
						this.tabList.remove(tag);
						// clear tabhost;
						this.th.clearAllTabs(false);
						// put back active tab
						this.th.addTab(this.main);
						String tabName[] = new String[this.tabList.size()];
						this.tabList.keySet().toArray(tabName);
						for (String cur : tabName)
						{
							this.th.addTab(this.tabList.get(cur));
						}
					}
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void removeBuddy(String id, String group)
	{
		if (this.aymsg != null)
			try
			{
				//remove buddy from the list
//				remove Buddy form group 
				AYmsgGroup group1 = this.aymsg.getGroupList().get(group);
				if (group1 != null)
					group1.getBuddyList().remove(id);
				
				//remove group from buddy
				AYmsgBuddy buddy = this.aymsg.getBuddyList().get(id);
				if (buddy != null)
				{
					buddy.getGroups().remove(group);

					// if this buddy does not contain in any group
					// remove reference
					if (buddy.getGroups().isEmpty())
					{
						this.aymsg.getBuddyList().remove(id);
					}
				}
				
				//if current group is empty then remove it
				AYmsgGroup tg;
				if ((tg = this.aymsg.getGroupList().get(group)) != null)
				{
					if (tg.getBuddyList().isEmpty())
					{
						this.aymsg.getGroupList().remove(group);
					}
				}
				this.doUpdateList();
				this.aymsg.removeBuddy(this.aymsg.getUsername(), id, group);
			} catch (IllegalArgumentException e)
			{
				// when one of the parameter empty
				e.printStackTrace();
			} catch (IllegalStateException e)
			{
				// When not login
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Set the Caption when no data
	 * 
	 * @param text
	 *            the caption
	 */
	public void setCaption(String text)
	{
		TextView txtCaption = (TextView) this.findViewById(R.id.status);
		txtCaption.setText(text);
	}
	
	/**
	 * Sample only for the ClientCallback now can change status;
	 * 
	 * @param type
	 *            AYmsgType
	 * @param Status
	 *            the status Text
	 */
	public void setCustomStatus(int type, String Status)
	{
		this.aymsg.setCustomStatus(AYmsgType.YAHOO_STATUS_AVAILABLE,
				this.getString(R.string.default_status));
		this.setCaption(Status);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, java.lang.String,
	 *      android.os.Bundle)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			String data, Bundle extras)
	{
		super.onActivityResult(requestCode, resultCode, data, extras);
		if (resultCode == RESULT_CANCELED)
			return;
		switch (requestCode)
		{
			case SET_STATUS:
				int status = extras.getInt(ClientStatus.STATUS_VALUE);
				boolean custom = extras
						.getBoolean(ClientStatus.USECUSTOM_VALUE);
				if (custom)
				{
					this.aymsg.setCustomStatus(status, data);
					this.setCaption(data);
				} else
				{
					try
					{
						this.aymsg.setStatus(status);
						switch (status)
						{
							case 0:
								this.setCaption("Available");
								break;
							case 1:
								this.setCaption("Be Right Back");
								break;
							case 2:
								this.setCaption("Busy");
								break;
							case 3:
								this.setCaption("Not At Home");
								break;
							case 4:
								this.setCaption("Not At Desk");
								break;
							case 5:
								this.setCaption("Not In Office");
								break;
							case 6:
								this.setCaption("On The Phone");
								break;
							case 7:
								this.setCaption("On Vacation");
								break;
							case 8:
								this.setCaption("Out to Lunch");
								break;
							case 9:
								this.setCaption("Steepd Out");
								break;
							case 12:
								this.setCaption("Invisible");
								break;
							default:
								this.setCaption("");
								break;
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				break;
			case ADD_BUDDY:
				String group = extras.getString(ClientBuddyAdd.GROUP);
				Log.d(TAG, "Add Buddy : " + data + " " + group);
				if (this.aymsg != null)
					this.aymsg.addBuddy(this.aymsg.getUsername(), data, group);
				break;
			case CREATE_CONF:
				if (this.aymsg != null)
				{
					try
					{
						//send invitation to all group member
						String confName = extras.getString(ClientCreateConf.CONF_NAME);
						String message = extras.getString(ClientCreateConf.MESSAGE);
						String buddy = extras.getString(ClientCreateConf.BUDDY);
						
						AYmsgConference cf = this.aymsg.getConferenceList().get(confName);
						if (cf == null) //not exists
						{
							cf = new AYmsgConference(confName);
							this.aymsg.getConferenceList().put(confName, cf);
						}
						
						//check for exsisting tab;
						TabHost.TabSpec cfTab = this.confTabList.get(confName); 
						if (cfTab == null) //not exists create new
						{
							String users[] = {this.aymsg.getUsername()};
							cfTab = this.newConferenceTab(confName,users);
							this.th.addTab(cfTab);
							this.confTabList.put(confName, cfTab);			
						}
						
						this.aymsg.conferenceLogOn(this.aymsg.getUsername(), confName);

						
						Log.d(TAG,"CreateConf : Conf room = " + confName + " to buddy : " + buddy + " message: " + message + " cf: " + cf);
						//send invitation
						this.aymsg.conferenceSendInvitation(buddy, cf, message);
						
					} catch (IllegalArgumentException e)
					{
						Log.e(TAG,"CreateConf error: ",e);
						e.printStackTrace();
					}
					catch (IllegalStateException e)
					{
						Log.e(TAG,"CreateConf error: ",e);
						e.printStackTrace();
					} catch (IOException e)
					{
						Log.e(TAG,"CreateConf error: ",e);
						e.printStackTrace();
					}
				}
			default:
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		this.aymsg.logout();
		this.aymsg.stopService();
		this.aymsg.unRegisterClientCallback();
		this.aymsg = null;
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		final IconifiedText cur = (IconifiedText) this.getListAdapter().getItem(
				position);
		if (cur.getType() == IconifiedText.TYPE_GROUP)
		{
			final AYmsgGroup group = cur.getGroup();
			new AlertDialog.Builder(ClientMain.this).setIcon(R.drawable.group)
					.setTitle(group.getId()).setItems(
							R.array.Group_menu_option,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which)
								{
									/* User clicked so do some stuff */
									switch (which)
									{
										case 0:// Remove Group
											ClientMain.this.aymsg.removeGroup(group.getId());
											break;
										default:
											break;
									}
								}
							}).show();
		} else
		// Type Buddy
		{
			final AYmsgBuddy buddy = cur.getBuddy();
			new AlertDialog.Builder(ClientMain.this)
					.setIcon(R.drawable.user_on).setTitle(buddy.getId())
					.setItems(R.array.buddy_menu_option,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which)
								{
									/* User clicked so do some stuff */
									switch (which)
									{
										case 0:// chat
											if (ClientMain.this.th != null)
											{
												if (ClientMain.this.tabList.get(buddy.getId()) == null)
												{
													TabHost.TabSpec ts = ClientMain.this.newChatTab(buddy
															.getId());
													ClientMain.this.th.addTab(ts);
													ClientMain.this.tabList.put(buddy.getId(),
															ts);
												}
												ClientMain.this.th.setCurrentTabByTag(buddy
														.getId());
											}
											break;
										case 1:// Remove Buddy
											ClientMain.this.removeBuddy(buddy.getId(), cur
													.getGroup().getId());
											break;
										case 2: //create Conference
											Intent i2 = new Intent(ClientMain.this, ClientCreateConf.class);
											
											Bundle extras = new Bundle();		
											
											//put buddy Id
											extras.putString(ClientCreateConf.BUDDY, buddy.getId());
											
											//generate list of conference											
											if (ClientMain.this.aymsg != null && ClientMain.this.aymsg.getConferenceList().size() > 0)
											{
												String confNames[] = new String[ClientMain.this.aymsg.getConferenceList().size()];
												ClientMain.this.aymsg.getConferenceList().keySet().toArray(confNames);
												
												Bundle cfList = new Bundle();
												for (String cfname : confNames)
												{
													cfList.putString(cfname, cfname);
												}
												
												extras.putBundle(ClientCreateConf.CONF_LIST, cfList);
											}
											
											
											i2.putExtras(extras);
											ClientMain.this.startSubActivity(i2, CREATE_CONF);
											break;
										default:
											break;
									}
								}
							}).show();
		}
	}
	
}