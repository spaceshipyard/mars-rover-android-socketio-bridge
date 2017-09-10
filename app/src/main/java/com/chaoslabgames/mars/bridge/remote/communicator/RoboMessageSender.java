package com.chaoslabgames.mars.bridge.remote.communicator;

import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;

import java.io.IOException;

/**
 * Created by drykovanov on 24.08.2017.
 */

public interface RoboMessageSender {
    void send(final RoboMessage msg) throws IOException;
}
