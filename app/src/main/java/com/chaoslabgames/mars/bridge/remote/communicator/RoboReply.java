package com.chaoslabgames.mars.bridge.remote.communicator;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class RoboReply {
    public String requestId;
    public Object body;

    public RoboReply(Object body, String requestId) {
        this.body = body;
        this.requestId = requestId;
    }
}
