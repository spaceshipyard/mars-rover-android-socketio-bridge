package com.chaoslabgames.mars.bridge.remote.communicator;

import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;

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
        final RoboMessage roboMessage = new RoboMessage();
        final String requestId = "id";
        roboMessage.requestId = requestId;
        this.sender.send(roboMessage);
        final RoboReply reply = reader.readReply();
        if (reply.requestId.equals(requestId)) {
            throw

        }

        return reply.body;
    }
}
