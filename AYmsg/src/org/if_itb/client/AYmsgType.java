/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.if_itb.client;


public class AYmsgType
{
	public static final int ADDIDENT = 0x10;
	public static final int ADDIGNORE = 0x11;
	public static final int AUDIBLE = 0xd0;
	public static final int AUTH = 0x57;
	public static final int AUTHRESP = 0x54;
	public static final int AVATAR = 0xbc;
	public static final int CALENDAR = 0xd;
	public static final int CHATADDINVITE = 0x9d;
	public static final int CHATCONNECT = 0x96;
	public static final int CHATDISCONNECT = 0xa0;
	public static final int CHATEXIT = 0x9b;
	public static final int CHATGOTO = 0x97;
	public static final int CHATINVITE = 0xc;
	public static final int CHATJOIN = 0x98;
	public static final int CHATLEAVE = 0x99;
	public static final int CHATLOGOFF = 0x1f;
	public static final int CHATLOGON = 0x1e;
	public static final int CHATMSG = 0xa8;
	public static final int CHATPING = 0xa1;
	public static final int CHATPM = 0x20;
	public static final int CONFADDINVITE = 0x1c;
	public static final int CONFDECLINE = 0x1a;
	public static final int CONFINVITE = 0x18;
	public static final int CONFLOGOFF = 0x1b;
	public static final int CONFLOGON = 0x19;
	public static final int CONFMSG = 0x1d;
	public static final int CONTACTIGNORE = 0x85;
	public static final int CONTACTNEW = 0xf;
	public static final int CONTACTREJECT = 0x86;
	public static final int FRIENDADD = 0x83;
	public static final int FILETRANSFER = 0x46;
	public static final int FRIENDREMOVE = 0x84;
	public static final int GAME_INVITE = 0xb7;
	public static final int GAMELOGOFF = 0x29;
	public static final int GAMELOGON = 0x28;
	public static final int GAMEMSG = 0x2a;
	public static final int GOTGROUPRENAME = 0x13;
	public static final int GROUPRENAME = 0x89;
	public static final int IDACT = 0x7;
	public static final int IDDEACT = 0x8;
	public static final int IDLE = 0x5;
	public static final int ISAWAY = 0x3;
	public static final int ISBACK = 0x4;
	public static final int KEEPALIVE = 0x8a;
	public static final int LIST = 0x55; //buddy list (onld version maybe, dont know)
	public static final int LOGOFF = 0x2;
	public static final int LOGON = 0x1;
	public static final int MAILSTAT = 0x9;
	public static final int MESSAGE = 0x6;
	public static final int NEWMAIL = 0xb;
	public static final int NEWPERSONMAIL = 0xe;
	public static final int NOTIFY = 0x4b;
	public static final int P2PFILEXFER = 0x4d;
	public static final int PASSTHROUGH2 = 0x16;
	public static final int PEERTOPEER = 0x4f;
	public static final int PICTURE = 0xbe;
	public static final int PICTURE_CHECKSUM = 0xbd;
	public static final int PICTURE_STATUS = 0xc7;
	public static final int PICTURE_UPDATE = 0xc1;
	public static final int PICTURE_UPLOAD = 0xc2;
	public static final int PING = 0x12;
	public static final int SKINNAME = 0x15;
	public static final int STEALTH_PERM = 0xb9;
	public static final int STEALTH_SESSION = 0xba;
	public static final int SYSMESSAGE = 0x14;
	public static final int UNKNOWN001 = 0xe4;
	public static final int UNKNOWN002 = 0xef;
	public static final int UNKNOWN003 = 0x3330;
	public static final int UNKNOWN005 = 0x6c6f;
	public static final int USERSTAT = 0xa;
	public static final int VERIFY = 0x4c;
	public static final int VERIFY_ID_EXISTS = 0xc8;
	public static final int VOICECHAT = 0x4a;
	public static final int WEBCAM = 0x50;
	public static final int X_BUZZ = 0xf03;
	public static final int X_CHATUPDATE = 0xf04;
	public static final int X_ERROR = 0xf00;
	public static final int X_EXCEPTION = 0xf02;
	public static final int X_OFFLINE = 0xf01;
	public static final int Y6_STATUS_UPDATE = 0xc6;
	public static final int Y6_VISIBLE_TOGGLE = 0xc5;
	public static final int Y7_AUTHORIZATION = 0xd6;
	public static final int Y7_CHANGE_GROUP = 0xe7;
	public static final int Y7_CHAT_SESSION = 0xd4;
	public static final int Y7_CONTACT_DETAILS = 0xd3;
	public static final int Y7_FILETRANSFER = 0xdc;
	public static final int Y7_FILETRANSFERACCEPT = 0xde;
	public static final int Y7_FILETRANSFERINFO = 0xdd;
	public static final int STATUS_15 = 0xf0; //Yahoo Status
	public static final int LIST_15 = 0xf1; //Yahoo Buddy List
	public static final int DC_CON_ERROR = 0x7d1; //Yahoo Buddy List
	public static final int DC_OTHER_LOGED = 0x3f7; //Yahoo Buddy List
	public static final int DC_AUTH_FAILED = 0x3f6; //Yahoo Buddy List
	
	
	
	
	// Home made service numbers, used in event dispatch only
	public static final int Y7_MINGLE = 0xe1;
	public static final int Y7_PHOTO_SHARING = 0xd2;
	public static final int YAB_UPDATE = 0xc4;
	public static final int YAHOO_SERVICE_SMS_MSG = 0x02ea;
	public static final int YAHOO_SERVICE_WEBLOGIN = 0x0226;
	public static final int AYMSG_ERROR = 0x0227;
	
	
	/* THIS IS CONSTATNT FOR STATUS */
	public final static int YAHOO_STATUS_DEFAULT = 0;
	public final static int YAHOO_STATUS_AVAILABLE = 0;
	public final static int YAHOO_STATUS_SERVER_ACK = 1;
	public final static int YAHOO_STATUS_BRB = 1;
	public final static int YAHOO_STATUS_BUSY= 2;
	public final static int YAHOO_STATUS_NOTATHOME = 3;
	public final static int YAHOO_STATUS_NOTATDESK = 4;
	public final static int YAHOO_STATUS_NOTINOFFICE = 5;
	public final static int YAHOO_STATUS_ONPHONE= 6;
	public final static int YAHOO_STATUS_ONVACATION= 7;
	public final static int YAHOO_STATUS_OUTTOLUNCH= 8;
	public final static int YAHOO_STATUS_STEPPEDOUT= 9;
	public final static int YAHOO_STATUS_INVISIBLE= 12;
	public final static int YAHOO_STATUS_NOTIFY= 22;
	public final static int YAHOO_STATUS_CUSTOM= 99;
	public final static int YAHOO_STATUS_IDLE= 999;
	public final static int YAHOO_STATUS_WEBLOGIN = 0x5A55AA55;
	public final static int YAHOO_STATUS_OFLINE = 0x5A55AA56;
	public final static int YAHOO_STATUS_TYPING = 0x16;
	public final static int YAHOO_STATUS_DISCONNECTED = 0xFFFFFFFF;	
	
	
	/**
	 * Returns the enum-value that matches the integer representation. Throws an
	 * IllegalArgumentException if no such enum value exists.
	 * 
	 * @param value
	 *            Integer value representing a ServiceType
	 * @return Returns the ServiceType associated with the integer value.
	 */
	/*
	 * public static AYmsgServiceType getServiceType(int value) { final
	 * AYmsgServiceType[] all = AYmsgServiceType.values(); for (int i = 0; i <
	 * all.length; i++) { if (all[i].getValue() == value) { return all[i]; } }
	 * Log.e("AYmsg","No such ServiceType value '" + value + "' (which is '" +
	 * Integer.toHexString(value) + "' in hex)."); return null; }
	 */
	
	
	private final int value;

	AYmsgType(final int value)
	{
		this.value = value;
	}

	/**
	 * Returns the integer value for this ServiceType.
	 * 
	 * @return the value
	 */
	public int getValue()
	{
		return this.value;
	}
}
