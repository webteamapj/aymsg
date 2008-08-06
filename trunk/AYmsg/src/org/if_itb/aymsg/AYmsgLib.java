/**
 * 
 */
package org.if_itb.aymsg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.if_itb.client.AYmsgType;
import android.util.Log;

/**
 * @author Ahmy Yulrizka 
 * This class is the main library that used by the service
 * what this class do is actually create a socket to the yahoo messenger server
 * and communicate with sending and receiving packets
 */
public class AYmsgLib implements Runnable
{
	public static final byte CONN_DISCONNECTED = 0x00;
	public static final byte CONN_CONNECTED = 0x01;
	public static final byte CONN_LOGGED = 0x02;
	private final String TAG = "AYmsg";
	private final byte[] header = { 0x59, 0x4d, 0x53, 0x47, 0x00, 0x0F, 0x00,
			0x00 };
	private final byte STATE_READING_YMSG = 0x1;
	private final byte STATE_READING_HEADER = 0x2;
	private final byte STATE_PREPARING_READ_KEY = 0x7;
	private final byte STATE_READING_KEY = 0x8;
	private final byte STATE_READED_C0_KEY_POSS_ERR = 0xA;
	private final byte STATE_PREPARE_READING_VALUE = 0xB;
	private final byte STATE_READED_C0_VAL_POSS_EMPTY = 0xC;
	
	private final byte STATE_READING_VAL = 0xD;
	private final byte STATE_END_KEY_VAL = 0xF;
	private final byte STATE_ERROR = 0x10;
	
	private byte connectionState = CONN_DISCONNECTED;

	
	private boolean flagStop = false;
	private boolean dcHandled = false;
	private AYmsgService parent;
	protected Socket socket = null;
	protected DataOutputStream dos = null;
	protected BufferedInputStream bis = null;
	
	protected InputStream is = null;
	
	Thread sThread = null;
	
	

	/**
	 * The main Constructor
	 */
	public AYmsgLib(AYmsgService aymsgservice)
	{
		this.parent = aymsgservice;
	}


	/**
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException
	{
		if (this.socket != null && this.socket.isConnected())
			this.socket.close();
		
		this.flagStop = false;
		this.connectionState = CONN_DISCONNECTED;
		this.socket = new Socket("scs.msg.yahoo.com", 5050);
		this.dos = new DataOutputStream(new BufferedOutputStream(this.socket
				.getOutputStream()));
		this.is = this.socket.getInputStream();
		this.bis = new BufferedInputStream(this.is,8000);
		
		
		//if anything is ok the socket are connected
		this.connectionState = CONN_CONNECTED;
	}


	/**
	 * @return the connectionState
	 */
	public byte getConnectionState()
	{
		return this.connectionState;
	}


	public void handleDisconnected(int dcType)
	{
		//stop ping timer
		
		
		List<String> key = new ArrayList<String>();
		key.add("1");
		List<String> value = new ArrayList<String>();
		key.add(String.valueOf(dcType));
		if (!isDcHandled()) // disconnect is not anticipated by client
			this.parent.sendCallback(key, value, AYmsgType.DC_CON_ERROR, AYmsgType.YAHOO_STATUS_DISCONNECTED, 0);
	}


	/**
	 * This function will create thread that will initiate socket connection
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void init() throws UnknownHostException, IOException
	{
			this.connect();	
	}

	
	/**
	 * @return the dcHandled , state that disconnection is anticipated / not by user
	 */
	public boolean isDcHandled()
	{
		return this.dcHandled;
	}
	
	public void reset()
	{
		this.flagStop = true;
		this.connectionState = CONN_DISCONNECTED;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		if (this.connectionState != CONN_CONNECTED)
			return;
		
		try
		{		
			// handle incoming packet			
			ByteBuffer buffer = ByteBuffer.allocate(0); //just initialization
			int retDataLen = 0;
			int retService = 0;
			int retStatus = 0;
			int retSessionId = 0;
			byte[] key;
			byte[] value;
			int lenRead = 0;
			int pos = 0;
			byte state = this.STATE_READING_YMSG;
			byte cur;
			List<String> keyList=new ArrayList<String>();
			List<String> valueList=new ArrayList<String>();
			
			Thread.interrupted();
			
			while (true)
			{
				
				//Got flag Stop, Exit loop 
				if (this.flagStop)
					break;
				
				try
				{
					if (state == this.STATE_READING_YMSG)
					{
						keyList = new ArrayList<String>();
						valueList = new ArrayList<String>();
						Log.d(this.TAG,"AYmsgLib: Reading Socket");
						buffer = ByteBuffer.allocate(20);
						if (this.bis.read(buffer.array(),0,20) == -1)
						{
							Log.e(this.TAG,"BufferArray read == -1");
							this.connectionState = CONN_DISCONNECTED;
							handleDisconnected(AYmsgType.DC_CON_ERROR);
							break;
						}
						
						long readHeader = buffer.getInt(); // check YMSG or 0x594D5347 (4 byte)
											
						if (readHeader == 0x594D5347)						
							state = this.STATE_READING_HEADER;
						
						Log.d(this.TAG, "state readig header");
					}
					if (state == this.STATE_READING_HEADER)
					{
						Log.d(this.TAG,"Start reading Yahoo! Packet");
						
						buffer.getInt(); // skip version 00 0F (2 byte)
						// and padding 00 00 (2 byte);
						retDataLen = buffer.getChar(); // Data Length (2 byte)
						Log.i(this.TAG,"Length : " + retDataLen);
						retService = buffer.getChar(); // Service id (2 byte)
						Log.i(this.TAG,"Service : " + retService);
						retStatus = buffer.getInt(); // Status (4 byte)
						Log.i(this.TAG,"Status : " + retStatus);
						retSessionId = buffer.getInt(); // Status (4 byte)
						Log.i(this.TAG,"Session Id : " + retSessionId);
						// reverse endiannes
						
						//resize buffer to the packet length
						buffer = ByteBuffer.allocate(retDataLen);
						
						/*int readed = bis.read(buffer.array(), 0, left); 
						left -= readed;
						buffer.limit(readed);
						Log.d(TAG,"left="+left);*/
						
						int p = 0, r = 0;
						while (p < retDataLen) {
							r = this.bis.read(buffer.array(), p, retDataLen - p);
							if (r < 0)
							{
								Log.e(this.TAG,"BufferArray read == -1");
								this.connectionState = CONN_DISCONNECTED;
								handleDisconnected(AYmsgType.DC_CON_ERROR);
								break;
							}
							p += r;
						}

						lenRead = 0;
						state = this.STATE_PREPARING_READ_KEY;
					}
					if (state == this.STATE_PREPARING_READ_KEY)
					{

						pos = buffer.position();
						cur = buffer.get();
						lenRead++;
						if (cur == -64)
						{
							state = this.STATE_READED_C0_KEY_POSS_ERR;
							
							cur = buffer.get();
							lenRead++;
							if (cur == -128)
							{							
								// Error while reading packet
								state = this.STATE_ERROR;
								return;
							}
						}
						state = this.STATE_READING_KEY;
					}
					String currkey= "";
					if (state == this.STATE_READING_KEY)
					{
						while (true)
						{			
							cur = buffer.get();						
							lenRead++;
							//Log.d(TAG,retDataLen + " " + lenRead + " cur=" + cur);
							if (cur == -64) // 0xc0
							{
								cur = buffer.get();
								lenRead++;
								if (cur == -128) // 0x80
								{						
									int lenKey = buffer.position() - pos - 2;
									key = new byte[lenKey];
									buffer.position(pos);
									buffer.get(key, 0, lenKey);
									buffer.getChar(); // skip 2 byte separator
									// 0xc0 0x80

									currkey = new String(key,"ISO-8859-1");
									//Log.d(TAG,"Read Key = " + currkey);
									keyList.add(currkey);

									state = this.STATE_PREPARE_READING_VALUE;
									break;
								}
							}
						}
					}
					if (state == this.STATE_PREPARE_READING_VALUE)
					{
						pos = buffer.position();
						cur = buffer.get();
						lenRead++;
						if (cur == -64)
						{
							state = this.STATE_READED_C0_VAL_POSS_EMPTY;
							cur = buffer.get();
							lenRead++;
							if (cur == -128)
							{
								// Error while reading packet
								//Log.d(TAG,"Read Key:value = " + currkey + ":EMPTY");
								valueList.add("");
								state = this.STATE_END_KEY_VAL;							
							} else
							{
								state = this.STATE_READING_VAL;
							}
						} else {
							state = this.STATE_READING_VAL; 
						}
					}
					if (state == this.STATE_READING_VAL)
					{
						while (true)
						{
							cur = buffer.get();
							lenRead++;
							if (cur == -64)
							{

								cur = buffer.get();
								lenRead++;
								if (cur == -128)
								{
									int lenKey = buffer.position() - pos - 2;
									value = new byte[lenKey];
									buffer.position(pos);
									buffer.get(value, 0, lenKey);
									buffer.getChar(); // skip 2 byte separator
									// 0xc0 0x80

									String currValue = new String(value,"ISO-8859-1");
									//Log.d(TAG,"Read key:value = " + currkey + ":"+ currValue);
									valueList.add(currValue);

									state = this.STATE_END_KEY_VAL;
									break;
								}
							}
						}
					}
					if (state == this.STATE_END_KEY_VAL)
					{
						// last byte is not worthed if not > 2 (0xc0 0x80) 
						if (lenRead + 2 >= retDataLen) 
						{
							// return complete packet
							state = this.STATE_READING_YMSG;
							Log.d(this.TAG,"Finish Receiving YAHOO! Packet");
							this.parent.sendCallback(keyList, valueList, retService, retStatus,retSessionId);
							
						} else
						{
							state = this.STATE_PREPARING_READ_KEY;
						}
					}
				} catch (BufferUnderflowException e)
				{
					Log.e(this.TAG,"Error : ",e);
					e.printStackTrace();
				} catch (RuntimeException e)
				{
					Log.e(this.TAG,"Run.Error : ",e);
					e.printStackTrace();
				}
			}// end While loop
		} catch (UnknownHostException e)
		{
			Log.e(this.TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e)
		{
			Log.e(this.TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @param service The service type. see AYmsgType
	 * @param status the connection status see AYmsgType
	 * @param sesionId the session id
	 * @param data the data to send
	 * @throws IOException
	 * Send a yahoo packet to server
	 */
	public void sendYahooPacket(int service, int status, int sesionId, byte [] data) throws IOException
			{
		try
		{
			if (!this.socket.isConnected())
				throw new IOException("Error in connection");
				
			int length;
			if (data == null)
				length =0;
			else 
				length = data.length;
			
			this.dos.write(this.header);
			this.dos.writeChar(length); // write 2 byte of length
			this.dos.writeChar(service); // write 2 byte
			this.dos.writeInt(status); // write 4 byte of status
			this.dos.writeInt(sesionId); // write 4 byte of session ID
			if (data !=null)
				this.dos.write(data); // write body
			this.dos.flush();
			
			Log.v(this.TAG, "Sent YMSG package");
			
		} catch (Exception e)
		{
			handleDisconnected(AYmsgType.DC_CON_ERROR);
			Log.e(this.TAG, "nah" + e.getMessage());
			e.printStackTrace();
		}
			}
	
	/**
	 * Send a Yahoo Packet to server
	 * 
	 * @param id
	 *            array of Key
	 * @param value
	 *            array of value that couple with id
	 * @param service
	 *            service type
	 * @param status
	 *            the status
	 * @param sesionId
	 *            session ID reverse. correct value must call with
	 *            Integer.Integer.reverseBytes()
	 * @throws IOException
	 *             Exception to be handle for socket error
	 */
	public void sendYahooPacket(String[] id, String[] value, int service,
			int status, int sesionId) throws IOException
	{
		short len = 0;
		int count = value.length;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		for (int i = 0; i < count; i++)
		{
			byte[] curId = id[i].getBytes();
			byte[] curValue = value[i].getBytes();
			bs.write(curId);
			bs.write(0xc0);
			bs.write(0x80);
			bs.write(curValue);
			bs.write(0xc0);
			bs.write(0x80);
			len += 4 + curId.length + curValue.length;
		}
		try
		{
			if (this.dos != null)
			{
				this.dos.write(this.header);
				this.dos.writeChar(len); // write 2 byte of length
				this.dos.writeChar(service); // write 2 byte
				this.dos.writeInt(status); // write 4 byte of status
				this.dos.writeInt(sesionId); // write 4 byte of session ID
				this.dos.write(bs.toByteArray()); // write body
				this.dos.flush();
				Log.v(this.TAG, "Sent YMSG package");
			} else
			{
				Log.e(this.TAG, "Data Output Stream Null");
			}
			


		} catch (IOException e)
		{
			Log.e(this.TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @param connectionState the connectionState to set
	 */
	public void setConnectionState(byte connectionState)
	{
		this.connectionState = connectionState;
	}

	/**
	 * @param dcHandled the dcHandled to set that disconnection is anticipated 
	 */
	public void setDcHandled(boolean dcHandled)
	{
		this.dcHandled = dcHandled;
	}

	public void startThread() throws UnknownHostException, IOException
	{
		this.init();
		this.sThread = new Thread(this);
		this.sThread.start();
		this.flagStop = false;
	}
	
	public void stopThread()
	{
		Log.d(this.TAG, "Thread Stop. Connection Close");
		if (this.socket != null)
			try
			{
				this.socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
						
		this.sThread = null;
	}
}