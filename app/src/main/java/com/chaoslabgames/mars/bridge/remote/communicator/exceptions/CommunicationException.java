package com.chaoslabgames.mars.bridge.remote.communicator.exceptions;

import java.io.IOException;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class CommunicationException extends Exception {
    public CommunicationException() {
    }

    public CommunicationException(Throwable throwable) {
        super(throwable);
    }
}
