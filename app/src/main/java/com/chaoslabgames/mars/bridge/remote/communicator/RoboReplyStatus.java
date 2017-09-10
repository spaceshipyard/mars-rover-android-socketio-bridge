package com.chaoslabgames.mars.bridge.remote.communicator;

/**
 * Created by drykovanov on 24.08.2017.
 */

public enum RoboReplyStatus {
    OK("OK"),
    UNKNOWN_CMD("Unknown command"),
    NotJSON("Not JSON"),
    UNKNOWN_STATUS("Unknown status");

    public final String name;

    private RoboReplyStatus(String name) {
        this.name = name;
    }

    public static RoboReplyStatus fromInt(int id) {
        switch (id) {
            case 0: return OK;
            case 1: return OK;
            case 2: return OK;
            default: return UNKNOWN_STATUS;
        }
    }

    @Override
    public String toString() {
        return "RoboReplyStatus{" +
                "name='" + name + '\'' +
                '}';
    }
}
