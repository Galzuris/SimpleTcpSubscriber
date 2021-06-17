package com.galzuris.simpletcpsubscriber;

public interface SimpleTcpSubscriberListener {
    void onTcpConnect(SimpleTcpSubscriber subscriber);
    void onTcpDisconnect(SimpleTcpSubscriber subscriber);
    void onTcpMessage(SimpleTcpSubscriber subscriber, String data);
    void onTcpError(SimpleTcpSubscriber subscriber, Exception e);
}
