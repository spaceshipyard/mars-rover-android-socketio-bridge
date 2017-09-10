package com.chaoslabgames.mars.bridge.remote.communicator;

import org.json.JSONObject;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class RoboReply {
    public String replyOn;
    public JSONObject params;
    public String cmd;

    public RoboReply(final JSONObject params, final String replyOn, final String cmd) {
        this.params = params;
        this.replyOn = replyOn;
        this.cmd = cmd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoboReply reply = (RoboReply) o;

        if (replyOn != null ? !replyOn.equals(reply.replyOn) : reply.replyOn != null)
            return false;
        if (!params.toString().equals(reply.params.toString())) return false;
        return cmd.equals(reply.cmd);

    }

    @Override
    public int hashCode() {
        int result = replyOn != null ? replyOn.hashCode() : 0;
        result = 31 * result + params.hashCode();
        result = 31 * result + cmd.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RoboReply{" +
                "replyOn='" + replyOn + '\'' +
                ", params=" + params +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
