package com.chaoslabgames.mars.bridge.remote.socketio;

import java.io.Serializable;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class AbstractArduinoBridge {
    private final InputMessageChannel inChannel;
    private final OuputMessageChannel outChannel;

    public AbstractArduinoBridge(OuputMessageChannel outputChannel, InputMessageChannel inChannel) {
        this.outChannel = outputChannel;
        this.inChannel = inChannel;
        inChannel.listener(new InputMessageChannel.MessageListener() {
            @Override
            public void onMessage(ArduinoMessage message) {

            }
        });
    }

    protected void onMessage(ArduinoMessage msg) {

    }

    public void sendMessage(ArduinoMessage message) {
        this.outChannel.pushMessage(message);
    }

    public interface OuputMessageChannel {
        void pushMessage(ArduinoMessage message);
    }

    public interface InputMessageChannel {
        void  listener(MessageListener listener);
        interface MessageListener {
            void onMessage(ArduinoMessage message);
        }
    }
}