package org.example.teamspeak3app.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.example.teamspeak3app.service.TS3ClientService;

public class ClientPaydayEvent implements TS3Listener {
    private final TS3Api ts3Api;
    private final TS3ClientService ts3ClientService;

    public ClientPaydayEvent(TS3Api ts3Api, TS3ClientService ts3ClientService) {
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
        Client client = ts3Api.getClientInfo(e.getClientId());
        if (client.isRegularClient()) {
            ts3ClientService.updateTS3ClientLastPayDay(client.getUniqueIdentifier());
        }
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
    }
}