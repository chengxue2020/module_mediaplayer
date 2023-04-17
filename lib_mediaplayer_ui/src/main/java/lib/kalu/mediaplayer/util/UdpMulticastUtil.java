package lib.kalu.mediaplayer.util;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public final class UdpMulticastUtil {

    public static boolean checkUdpJoinGroup(@NonNull String s) {
        try {
            // 1
            Uri uri = Uri.parse(s);
            String host = uri.getHost();
            int port = uri.getPort();
            // 2
            InetAddress address = InetAddress.getByName(host);
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            boolean multicastAddress = address.isMulticastAddress();
            if (!multicastAddress)
                throw new Exception(s + "not MulticastAddress");
            // 3
            MulticastSocket socket = new MulticastSocket(socketAddress);
            socket.setSoTimeout(200);
            socket.joinGroup(address);
            socket.setLoopbackMode(false); // 必须是false才能开启广播功能！！
            // 4
            DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength());
            // 5
            socket.leaveGroup(address);
            socket.close();
            if (null == msg || msg.length() <= 0)
                throw new Exception("receive message is null : " + msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}