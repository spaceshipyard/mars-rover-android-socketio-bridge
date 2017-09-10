package com.chaoslabgames.mars.bridge.remote.communicator;

import org.json.JSONObject;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class RoboReply {
    public String replyOn;
    public RoboReplyStatus replyStatus;
    public JSONObject params;
    public String cmd;

    public RoboReply(final JSONObject params, final String replyOn,  final RoboReplyStatus replyStatus, final String cmd) {
        this.params = params;
        this.replyOn = replyOn;
        this.replyStatus = replyStatus;
        this.cmd = cmd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoboReply reply = (RoboReply) o;

        if (replyOn != null ? !replyOn.equals(reply.replyOn) : reply.replyOn != null) return false;
        if (replyStatus != reply.replyStatus) return false;
        if (params != null ? !params.toString().equals(reply.params.toString()) : reply.params != null) return false;
        return cmd != null ? cmd.equals(reply.cmd) : reply.cmd == null;

    }

    @Override
    public int hashCode() {
        int result = replyOn != null ? replyOn.hashCode() : 0;
        result = 31 * result + (replyStatus != null ? replyStatus.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (cmd != null ? cmd.hashCode() : 0);
        return result;
    }
}
