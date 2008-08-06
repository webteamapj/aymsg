package org.if_itb.aymsg;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.if_itb.client.AYmsgType;
import android.app.Service;
import android.content.Intent;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

/**
 * @author ahmy
 * This is the main Service. its implement the android Service block
 */
public class AYmsgService extends Service
{
	private static final String TAG = "AYmsg";
	private AYmsgLib aymsg = new AYmsgLib(this); //library
	private final IAYmsgService.Stub mBinder = new IAYmsgService.Stub() {
		
	
		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#setDisconnectHandled(boolean)
		 */
		public void setDisconnectHandled(boolean handled) throws DeadObjectException
		{
			AYmsgService.this.aymsg.setDcHandled(handled);
		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#getState()
		 */
		public byte getState() throws DeadObjectException
		{
			if (AYmsgService.this.aymsg != null)
				return AYmsgService.this.aymsg.getConnectionState();

			return -1;
		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#login(java.lang.String, java.lang.String)
		 */
		public void login(String username, String password)
		throws DeadObjectException
		{
			String[] key = { "1" };
			String[] value = new String[1];
			value[0] = username;
			try
			{
				Log.d(TAG,"AYmsgService : starting thread ");
				AYmsgService.this.aymsg.startThread();
				AYmsgService.this.aymsg.sendYahooPacket(key, value, 0x57, 0, 0);
			} catch (UnknownHostException e)
			{
				AYmsgService.this.sendErrorCallback("Error in Connection : Unknown Host " + e.getMessage());
				e.printStackTrace();
			}
			catch (IOException e)
			{
				AYmsgService.this.sendErrorCallback("Error in Connection : " + e.getMessage());
				Log.e(TAG,"Login error: ",e);
				e.printStackTrace();
			}			
		}


		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#registerCallback(org.if_itb.aymsg.IAYmsgServiceCallback)
		 */
		public void registerCallback(IAYmsgServiceCallback cbs)
		throws DeadObjectException
		{
			if (cbs != null)
				AYmsgService.this.mCallbacks.register(cbs);
		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#sendPacket(int, int, int, byte[])
		 */
		public void sendPacket(int service, int status, int sesionId, byte [] data) throws DeadObjectException
		{
			try
			{
				AYmsgService.this.aymsg.sendYahooPacket(service, status, sesionId,data);
			} catch (IOException e)
			{
				Log.e(TAG,"AYmsgService.Sendpacket : ",e);
				e.printStackTrace();
			}	
		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#setState(byte)
		 */
		public void setState(byte state) throws DeadObjectException
		{
			if (AYmsgService.this.aymsg != null)
				AYmsgService.this.aymsg.setConnectionState(state);
		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#startThread()
		 */
		public void startThread() throws DeadObjectException
		{
			if (AYmsgService.this.aymsg != null)
				try
			{
					AYmsgService.this.aymsg.startThread();
			} catch (UnknownHostException e)
			{
				AYmsgService.this.sendErrorCallback("Unknown host : " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e)
			{
				AYmsgService.this.sendErrorCallback("Start thread IO : " + e.getMessage());
				e.printStackTrace();
			}

		}

		/* (non-Javadoc)
		 * @see org.if_itb.aymsg.IAYmsgService#stopThread()
		 */
		public void stopThread() throws DeadObjectException
		{
			if (AYmsgService.this.aymsg != null)
				AYmsgService.this.aymsg.stopThread();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.if_itb.aymsg.IAYmsgService#unregisterCallback(org.if_itb.aymsg.IAYmsgServiceCallback)
		 */
		public void unregisterCallback(IAYmsgServiceCallback cb)
		throws DeadObjectException
		{
			if (cb != null)
				AYmsgService.this.mCallbacks.unregister(cb);
		}


	};

	/**
	 * The Callbacks
	 */
	final RemoteCallbackList<IAYmsgServiceCallback> mCallbacks = new RemoteCallbackList<IAYmsgServiceCallback>();

	/**
	 * Send Error message to client side
	 * @param msg the error emssage
	 */
	private void sendErrorCallback (String msg)
	{
		List<String> keys = new ArrayList<String>();
		keys.add("1");
		List<String> vals = new ArrayList<String>();
		vals.add(msg);

		this.sendCallback(keys, vals, AYmsgType.AYMSG_ERROR, 0, 0);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		if (IAYmsgService.class.getName().equals(intent.getAction()))
		{
			Log.i(TAG, "AYmsgService: onBind");
			return this.mBinder;
		}
		return null;
	}

	/**
	 * Send Callback to Client side
	 * @param key the keys
	 * @param value the values
	 * @param Service service type, see AYmsgType
	 * @param Status status type, see AYmsgType
	 * @param SessionId the session id filed
	 */
	public void sendCallback(List<String> key, List<String> value, int Service, int Status, int SessionId)
	{
//		Broadcast to all clients the new value.
		final int N = this.mCallbacks.beginBroadcast();
		for (int i=0; i<N; i++) {
			try {
				this.mCallbacks.getBroadcastItem(i).rawPacketHandler(key, value, Service, Status, SessionId );
			} catch (DeadObjectException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		this.mCallbacks.finishBroadcast();
	}

	@Override
	protected void onCreate()
	{
		Log.i(TAG, "on create");
	}
}