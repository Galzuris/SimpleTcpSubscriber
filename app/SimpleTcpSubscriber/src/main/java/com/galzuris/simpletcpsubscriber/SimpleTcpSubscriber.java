package com.galzuris.simpletcpsubscriber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SimpleTcpSubscriber {

    private final SimpleTcpSubscriberListener listener;
    private Thread loopThread = null;
    private Socket socket = null;
    private BufferedWriter writer;
    private BufferedReader reader;

    public SimpleTcpSubscriber(SimpleTcpSubscriberListener eventsListener)
    {
        this.listener = eventsListener;
    }

    public void connect(final String host, final int port)
    {
        if (loopThread != null) disconnect();
        loopThread = new Thread(() -> {
            try {
                socket = new Socket(host, port);
                if (listener != null) listener.onTcpConnect(this);
                loop(socket);
            }
            catch (Exception e) {
                if (listener != null) listener.onTcpError(this, e);
            }
        });
        loopThread.start();
    }

    public void disconnect()
    {
        if (loopThread != null && !loopThread.isInterrupted()) {
            loopThread.interrupt();
            loopThread = null;
        }
        try {
            reader.close();
            writer.close();

            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        }
        catch (Exception ignored) { }
    }

    private void send(final String data)
    {
        try {
            if (socket != null && socket.isConnected()) {
                if (writer != null) {
                    writer.write(data);
                }
            }
        }
        catch (Exception e) {
            if (listener != null) listener.onTcpError(this, e);
        }
    }

    private void loop(final Socket socket)
    {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStreamReader);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(outputStreamWriter);

            while (socket.isConnected()) {
                String message = reader.readLine();
                if (listener != null) listener.onTcpMessage(this, message.trim());
            }
        }
        catch (Exception e) {
            if (listener != null) listener.onTcpError(this, e);
            disconnect();
        }
    }

}
