package org.example.teamspeak3app.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.example.teamspeak3app.model.TS3Client;
import org.example.teamspeak3app.service.TS3ClientService;

public class ClientKickEvent implements TS3Listener {
    private final TS3Api ts3Api;
    private final TS3ClientService ts3ClientService;

    public ClientKickEvent(TS3Api ts3Api, TS3ClientService ts3ClientService) {
        this.ts3Api = ts3Api;
        this.ts3ClientService = ts3ClientService;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
    }


    @Override
    public void onServerEdit(ServerEditedEvent serverEditedEvent) {

    }

    @Override
    public void onChannelEdit(ChannelEditedEvent channelEditedEvent) {

    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {

    }

    @Override
    public void onClientMoved(ClientMovedEvent clientMovedEvent) {

    }

    @Override
    public void onChannelCreate(ChannelCreateEvent channelCreateEvent) {

    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {

    }

    @Override
    public void onChannelMoved(ChannelMovedEvent channelMovedEvent) {

    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {

    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {

    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        System.out.println(e);
        switch(e.getReasonId()){
            case 5:
            case 6: {
                String userId = e.getInvokerUniqueId();
                Client client = ts3Api.getClientInfo(e.getInvokerId());
                if (client.isRegularClient()) {
                    TS3Client ts3Client = ts3ClientService.updateTS3ClientOnDeleteChannel(userId);
                    if (ts3Client != null &&
                            ts3Client.getLastActionTime() != null &&
                            ts3Client.getNoOfActions() >= 3) {
                        ts3Api.kickClientFromServer("Mai usor cu spamul, printule!", client);
                        ts3Client.setLastActionTime(null);
                        ts3Client.setNoOfActions(0);
                        ts3ClientService.updateTS3Client(ts3Client);
                    }
                }
            }
                break;
        }
    }
}