package org.if_itb.aymsg;

interface IAYmsgServiceCallback
{
 void rawPacketHandler(in List<String> key, in List<String> value, in int Service,in int Status, in int SessionId);
}