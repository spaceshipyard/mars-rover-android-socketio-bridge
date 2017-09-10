package com.chaoslabgames.mars.bridge.remote.communicator.exceptions;

import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyStatus;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class BadReplyStatusException extends CommunicationException {
    public BadReplyStatusException(final RoboReplyStatus status) {
        super("Unexpected reply " + status);
    }
}
