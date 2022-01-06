package com.example.blind_test.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//To implement a terminal version.
public class TerminalClientImpl extends ClientImpl {
    private static TerminalClientImpl instance = new TerminalClientImpl();
    private static final Logger logger = LoggerFactory.getLogger(TerminalClientImpl.class);

    private TerminalClientImpl() {
    }

    public static TerminalClientImpl getUniqueInstanceOfTerminalClientImpl(){
        return instance;
    }


    @Override
    public void logoutSucceeded(String responseData) {

    }

    @Override
    public void connectSucceeded(String responseData) {

    }

    @Override
    public void connectFailed(String responseData) {

    }

    @Override
    public void registerSucceeded(String responseData) {

    }

    @Override
    public void registerFailed(String responseData) {

    }

    @Override
    public void createChannelSucceeded(String responseData) {

    }

    @Override
    public void createChannelFailed(String responseData) {

    }

    @Override
    public void joinChannelSucceed(String responseData) {

    }

    @Override
    public void joinChannelFailed(String responseData) {

    }

    @Override
    public void joinChannelBroadcastSucceeded(String responseData) {

    }

    @Override
    public void joinChannelBroadcastFailed(String responseData) {

    }

    @Override
    public void leaveChannelSucceeded(String responseData) {

    }

    @Override
    public void leaveChannelFailed(String responseData) {

    }

    @Override
    public void leaveChannelBroadcastSucceeded(String responseData) {

    }

    @Override
    public void leaveChannelBroadcastFailed(String responseData) {

    }

    @Override
    public void deleteMessageSucceeded(String responseData) {

    }

    @Override
    public void deleteMessageFailed(String responseData) {

    }

    @Override
    public void deleteMessageBroadcastSucceeded(String responseData) {

    }

    @Override
    public void deleteMessageBroadcastFailed(String responseData) {

    }

    @Override
    public void modifyMessageBroadcastSucceeded(String responseData) {

    }

    @Override
    public void modifyMessageBroadcastFailed(String responseData) {

    }

    @Override
    public void deleteChannelBroadcastSucceeded(String responseData) {

    }

    @Override
    public void deleteChannelBroadcastFailed(String responseData) {

    }

    @Override
    public void modifyChannelSucceeded(String responseData) {

    }

    @Override
    public void modifyChannelFailed(String responseData) {

    }

    @Override
    public void modifyChannelBroadcastSucceeded(String responseData) {

    }

    @Override
    public void modifyChannelBroadcastFailed(String responseData) {

    }

    @Override
    public void modifyMessageSucceeded(String responseData) {

    }

    @Override
    public void modifyMessageFailed(String responseData) {

    }

    @Override
    public void deleteChannelSucceeded(String responseData) {

    }

    @Override
    public void deleteChannelFailed(String responseData) {

    }

    @Override
    public void listChannelsInServerSucceeded(String responseData) {

    }

    @Override
    public void listChannelsInServerFailed(String responseData) {

    }

    @Override
    public void listOfMessageInChannelSucceeded(String responseData) {

    }

    @Override
    public void listOfMessageInChannelFailed(String responseData) {

    }

    @Override
    public void listOfJoinedChannelsSucceeded(String responseData) {

    }

    @Override
    public void listOfJoinedChannelsFailed(String responseData) {

    }

    @Override
    public void listOfUnJoinedChannelsSucceeded(String responseData) {

    }

    @Override
    public void listOfUnJoinedChannelsFailed(String responseData) {

    }

    @Override
    public void listOfUserInChannelSucceeded(String responseData) {

    }

    @Override
    public void listOfUserInChannelFailed(String responseData) {

    }

    @Override
    public void listOfRequestsSucceeded(String responseData) {

    }

    @Override
    public void listOfRequestsFailed(String responseData) {

    }

    @Override
    public void messageConsumed(String responseData) {

    }

    @Override
    public void messageConsumptionError(String responseData) {

    }

    @Override
    public void messageBroadcastSucceed(String responseData) {

    }

    @Override
    public void messageBroadcastFailed(String responseData) {

    }

    @Override
    public void joinPrivateChannel(String responseData) {

    }

    @Override
    public void requestAlreadySent(String responseData) {

    }

    @Override
    public void responseRequestJoinChannelSucceeded(String responseData) {

    }

    @Override
    public void responseRequestJoinChannelFailed(String responseData) {

    }

    @Override
    public void deleteUserSucceeded(String responseData) {

    }

    @Override
    public void deleteUserFailed(String responseData) {

    }

    @Override
    public void deleteUserBroadcastFailed(String responseData) {

    }

    @Override
    public void deleteUserBroadcastSucceeded(String responseData) {

    }
}
