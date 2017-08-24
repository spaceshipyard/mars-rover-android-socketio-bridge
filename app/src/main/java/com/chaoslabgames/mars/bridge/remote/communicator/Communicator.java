package com.chaoslabgames.mars.bridge.remote.communicator;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class Communicator {
    RoboMessageSender sender;
    RoboReplyReader reader;

    public Communicator(RoboMessageSender sender, RoboReplyReader reader) {
        this.sender = sender;
        this.reader = reader;
    }

    public Object send(Object msg) throws CommunicationException {
        this.sender.send(new RoboMessage());
        RoboReply reply = reader.readReply();

        return reply.body;
    }
}
