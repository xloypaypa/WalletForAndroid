package com.example.xsu.walletforandroid.net;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xsu on 16/8/21.
 * it's the net service
 */
public class NetService extends Service {

    private volatile Socket socket = null;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;

    private volatile Handler handler;

    private volatile String ip = null;
    private volatile int port = -1;

    private volatile BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service", "bind");
        return new NetBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.handler = null;
        Log.d("service", "unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread() {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (socket != null && socket.isClosed()) {
                            socket = null;
                            inputStream = null;
                            outputStream = null;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (socket == null && ip != null && port != -1) {
                            socket = new Socket(ip, port);
                            inputStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (outputStream != null) {
                            byte[] message = sendQueue.take();
                            byte[] head = (message.length + "x").getBytes();
                            byte[] result = new byte[head.length + message.length];
                            System.arraycopy(head, 0, result, 0, head.length);
                            System.arraycopy(message, 0, result, head.length, message.length);
                            Log.i("send message", new String(result));
                            outputStream.write(result);
                        }
                    } catch (InterruptedException ignored) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            private List<Byte> message = new ArrayList<>();

            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (inputStream != null) {
                            int len = inputStream.read(buffer);
                            for (int i = 0; i < len; i++) {
                                message.add(buffer[i]);
                            }
                            //noinspection StatementWithEmptyBody
                            while (splitPackage());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            private boolean splitPackage() {
                for (int i = 0; i < message.size(); i++) {
                    if (message.get(i) == 'x') {
                        byte[] head = new byte[i];
                        for (int j = 0; j < i; j++) {
                            head[j] = message.get(j);
                        }
                        int len = Integer.parseInt(new String(head));
                        if (message.size() - i - 1 >= len) {
                            byte[] result = new byte[len];
                            for (int j = 0; j < len; j++) {
                                result[j] = message.get(i + j + 1);
                            }
                            for (int j = 0; j < len + i + 1; j++) {
                                message.remove(0);
                            }
                            Log.i("get message", new String(result));
                            sendToUI(result);
                        }
                        return true;
                    }
                }
                return false;
            }

            private void sendToUI(byte[] result) {
                if (handler != null) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    int index;
                    for (index = 0; index < result.length; index++) {
                        if (result[index] == '#') {
                            break;
                        }
                    }
                    byte[] command = new byte[index];
                    byte[] body = new byte[result.length - index - 1];

                    System.arraycopy(result, 0, command, 0, index);
                    System.arraycopy(result, index + 1 , body, 0, result.length - index - 1);

                    bundle.putString("command", new String(command));
                    bundle.putByteArray("body", body);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    public class NetBinder extends Binder {

        public void setHandler(Handler handler) {
            NetService.this.handler = handler;
        }

        public void connect(String ip, int port) {
            NetService.this.ip = ip;
            NetService.this.port = port;
        }

        public void sendMessage(byte[] message) throws InterruptedException {
            NetService.this.sendQueue.put(message);
        }

    }
}
