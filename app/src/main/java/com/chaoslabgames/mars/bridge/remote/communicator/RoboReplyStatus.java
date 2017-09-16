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

    public static RoboReplyStatus fromKey(String key) {
        /**
         #define COMMAND_RESULT_PROCESSED "processed"
         #define COMMAND_RESULT_UNKNOWN_CMD "unknownCMD"
         #define COMMAND_RESULT_NOT_JSON "notJson"
         #define COMMAND_RESULT_UNKNOWN "unknown"
         */
        switch (key) {
            case "processed": return OK;
            case "unknownCMD": return UNKNOWN_CMD;
            case "notJson": return NotJSON;
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
