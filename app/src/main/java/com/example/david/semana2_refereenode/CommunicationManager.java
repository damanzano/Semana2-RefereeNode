package com.example.david.semana2_refereenode;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by David on 25/08/2016.
 */
public class CommunicationManager extends Observable implements Runnable {
    private static final String TAG = "CommunicationManager";
    private static CommunicationManager ref;
    // Default destination address - emulator host IP address
    public static final String DEFAULT_ADDRESS = "10.0.2.2";
    // A multicast IP address
    public static final String MULTI_GROUP_ADDRESS = "224.2.2.2";
    // Default destination port
    public static  final int DEFAULT_PORT = 5000;

    private MulticastSocket ms;
    private DatagramSocket ds;
    private boolean running;
    private boolean connecting;
    private boolean reset;
    private boolean errorNotified;

    private CommunicationManager() {
        running = true;
        connecting = true;
        reset = false;
        errorNotified = false;
        Log.d(TAG, "[ CommunicationManager Instance Built ]");
    }

    /**
     * Obtains the singleton instance of the CommunicationManager
     *
     * @return
     */
    public static CommunicationManager getInstance() {
        if (ref == null) {
            ref = new CommunicationManager();
            Thread runner = new Thread(ref);
            runner.start();
        }

        return ref;
    }

    @Override
    public void run() {
        Log.d(TAG, "[ Communication Thread Started ]");
        while (running) {
            if (connecting) {
                if (reset) {
                    if (ms != null) {
                        ms.close();
                        Log.d(TAG, "[Communication was reset]");
                    }
                    reset = false;
                }
                connecting = !attemptConnection();
            } else {
                if (ms != null) {
                    DatagramPacket p = receiveMessage();

                    // Validate that there are no errors with the data
                    if (p != null) {
                        // Transform packet bytes to understandable data
                        String message = new String(p.getData(), 0, p.getLength());

                        // Notify the observers that new data has arrived and pass the data to them
                        setChanged();
                        notifyObservers(message);
                        clearChanged();
                    }
                }
            }
        }
        ms.close();
    }

    private boolean attemptConnection() {
        try {
            ms = new MulticastSocket();
            setChanged();
            notifyObservers("Connection started");
            clearChanged();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "[ Error starting Communication]");
            if (!errorNotified) {
                setChanged();
                notifyObservers("Connection failed");
                clearChanged();
                errorNotified = true;
            }
            return false;
        }
    }

    public void sendMessage(final String message, final String destAddress, final int destPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ms != null) {

                    try {
                        // Validate destAddress
                        InetAddress ia = InetAddress.getByName(destAddress);
                        byte[] data = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(data, data.length, ia, destPort);

                        System.out.println("Sending data to " + ia.getHostAddress() + ":" + destPort);
                        ms.send(packet);
                        System.out.println("Data was sent");

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    setChanged();
                    notifyObservers("Not connected");
                    clearChanged();
                }
            }
        }).start();

    }

    public DatagramPacket receiveMessage() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            ms.receive(packet);
            System.out.println("Data received from " + packet.getAddress() + ":" + packet.getPort());
            return packet;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
