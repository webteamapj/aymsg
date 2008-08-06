package org.if_itb.aymsg;

import org.if_itb.aymsg.IAYmsgServiceCallback;
interface IAYmsgService
{
 /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void registerCallback(IAYmsgServiceCallback cbs);
    
    /**
     * Remove a previously registered callback interface.
	*/
    void unregisterCallback(IAYmsgServiceCallback cb);

	/** 
	* do the login procedure
	* @param username the Yahoo! Id
	* @param password the Password
	*/
	void login(in String username, in String password);
	
	/** 
	* Start the main thread
	*/
	void startThread();

	/** 
	* Stop the main trhead
	*/
	void stopThread();
	
	/** 
	* Send packet to Yahoo! server
	* @param service the Service type see AYmsgType
	* @param status the the status see AYmsgType
	* @param sessionId current session id
	* @param data ata to send
	*/
	void sendPacket(in int service, in int status, in int sesionId, in byte [] data);
	
	/** 
	* Get the connection state
	*/
	byte getState();
	
	/** 
	* Tell service that activity allready handle the Disconnect
	* @param handled boolean value that set wherter allready handle or not
	*/	
	void setDisconnectHandled(boolean handled);
	
	/** 
	* Set the connection state
	* @param state the connection state
	*/
	void setState(byte state);
}