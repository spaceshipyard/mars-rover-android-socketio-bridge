package com.chaoslabgames.mars.bridge.remote.communicator;

import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;

/**
 * Created by drykovanov on 24.08.2017.
 */

public interface RoboReplyReader {
    RoboReply readReply() throws CommunicationException;
}
