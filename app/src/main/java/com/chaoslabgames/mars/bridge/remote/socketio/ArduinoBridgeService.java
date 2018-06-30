package com.chaoslabgames.mars.bridge.remote.socketio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

/**
 * Created by drykovanov on 29.07.2017.
 */

public class ArduinoBridgeService extends Service {

    final int NOTIF_ID = 1;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        volatile boolean isSocketAlive = false;
        volatile String currentRoom;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            final String dispatcherUrl = msg.getData().getString("dispatcherUrl");
            final String roomName = msg.getData().getString("roomName");

            startForeground(NOTIF_ID, buildStatusNotification());

            initiateConnections(dispatcherUrl, roomName);
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }

        private Notification buildStatusNotification() {
            return new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.stat_notify_sync)
                    .setContentTitle("Service")
                    .setContentText(
                                    " dispatcher: " + isSocketAlive + " room: " + currentRoom).build();
        }

        private OkHttpClient getUnsafeOkHttpClient() throws Exception {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                OkHttpClient okHttpClient = builder.build();
                return okHttpClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void initiateConnections(String dispatcherUrl, final String roomName) {
            Toast.makeText(getApplicationContext(), "Bluetooth Service Started", Toast.LENGTH_LONG).show();

            try {
                OkHttpClient okHttpClient = getUnsafeOkHttpClient();
// default settings for all sockets
                IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
                IO.setDefaultOkHttpCallFactory(okHttpClient);

                final Socket socket = IO.socket(dispatcherUrl);

                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                            printMessageOnScreen("socket io connected");
                            isSocketAlive = true;
                            updateStatus();
                        }
                })
                .on("welcome", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            try {
                                JSONObject obj = (JSONObject) args[0];
                                currentRoom = obj.getJSONArray("currRooms").toString();

                                printMessageOnScreen("welcome message is received");
                                socket.emit("join", new JSONObject("{ roomName:\"" + roomName + "\" }"));
                                updateStatus();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("socket", e.getMessage(), e);
                            }
                        }
                })
                .on("join", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final JSONObject obj = (JSONObject) args[0];
                        try {
                            final String roomName;
                            roomName = obj.getString("roomName");
                            currentRoom = roomName;
                            updateStatus();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("socket", "join is failed " + e.getMessage(), e);
                        }
                    }
                })
                .on("event", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject obj = (JSONObject) args[0];
                                Log.d("event", obj.toString());
                                printMessageOnScreen("event " + obj.toString());
                            }

                        }).on("message", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final JSONObject obj = (JSONObject) args[0];
                        Log.d("message", obj.toString());
                        try {
                            final String cmd = obj.get("cmd").toString();
                            processCmd(cmd, obj.getJSONObject("params"));
                            printMessageOnScreen("in-cmd: " + cmd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("socket", "fail to get cmd", e);
                        }
                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("disconnected", args.toString());
                        printMessageOnScreen("socket is disconnected");
                        isSocketAlive = false;
                        updateStatus();

                    }

                }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        Log.d("socket", "error " + args.toString());
                        printMessageOnScreen("socket error " + args.toString());
                        isSocketAlive = false;
                        updateStatus();
                    }
                });
                socket.connect();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void updateStatus() {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIF_ID, buildStatusNotification());
        }

        private void processCmd(String cmd, JSONObject parameters) {
            if ("makeCall".equals(cmd)) {
                final String participants;
                try {
                    participants = parameters.getString("participants");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("socket", "cant get participants", e);
                    return;
                }
                final boolean video = parameters.optBoolean("video", false);
                makeCall(participants, video);
            } else {
                forwardToBT(cmd);
            }
        }

        private void makeCall(String participants, boolean video) {
            Intent skype = new Intent("android.intent.action.VIEW");
            skype.setData(Uri.parse("skype:" + participants + "?call" + (video ? "&video" : "")));
            skype.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(skype);
        }

        private void forwardToBT(String cmd) {

        }

        void printMessageOnScreen(String text) {
            //fixme recover
            //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.getData().putString("roomName", intent.getExtras().getString("roomName"));
        msg.getData().putString("btMacAddress", intent.getExtras().getString("btMacAddress"));
        msg.getData().putString("dispatcherUrl", intent.getExtras().getString("dispatcherUrl"));
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}