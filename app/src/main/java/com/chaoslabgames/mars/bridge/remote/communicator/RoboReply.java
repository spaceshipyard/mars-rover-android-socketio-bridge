package com.chaoslabgames.mars.bridge.remote.communicator;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class RoboReply {
    RoboReplyStatus status;
    String requestId;
    Object body;

    public RoboReply(Object body) {
        this.body = body;
    }
}
