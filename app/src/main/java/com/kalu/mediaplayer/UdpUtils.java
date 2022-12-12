package com.kalu.mediaplayer;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class UdpUtils {

    private final static String BC_IP = "220.220.220.220"; // 组播地址
    private final static int BC_PORT = 1234; // 组播端口
    private final static int PACK_SIZE = 4096;

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

    public static void init() throws IOException {
        MulticastSocket sock = new MulticastSocket(BC_PORT);
        InetAddress bcAddr = InetAddress.getByName(BC_IP);
        try (Scanner scan = new Scanner(System.in)) {
            // 创建socket并加入组播地址
            sock.joinGroup(bcAddr);
            sock.setLoopbackMode(false); // 必须是false才能开启广播功能！！

            new Thread(() -> { // 接受广播消息的线程
                try {
                    DatagramPacket inpack = new DatagramPacket(new byte[PACK_SIZE], PACK_SIZE);
                    while (true) {
                        sock.receive(inpack);
                        Log.e("UUDDPP", "广播消息：" + new String(inpack.getData(), 0, inpack.getLength()));
                    }
                } catch (IOException e) {
                    Log.e("UUDDPP", e.getMessage());
//                    e.printStackTrace();

                    if (sock != null) {
                        try {
                            sock.leaveGroup(bcAddr);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        sock.close();
                    }
                }
            }).start();

            // 主线程接受控制台输入并广播出去
            DatagramPacket outpack = new DatagramPacket(new byte[0], 0, bcAddr, BC_PORT); // 目的端口和MulticastSocket端口一样！！
            while (scan.hasNextLine()) {
                byte[] buf = scan.nextLine().getBytes();
                outpack.setData(buf);
                sock.send(outpack);
            }
        } finally { // 最终关闭程序之前一定要关闭socket
            sock.close();
        }
    }
}