package org.example.teamspeak3app.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class WelcomeMessageEvent implements TS3Listener {
    private final TS3Api api;

    public WelcomeMessageEvent(TS3Api api) {
        this.api = api;
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
        Client client = api.getClientInfo(e.getClientId());
        if (client.isRegularClient()) {
            String stringBuilder =
                    "Hello [B]" + client.getNickname() + "[/B]! Bine ai venit pe [B]" + api.getServerInfo().getName() + "[/B]\n" +
                            "UniqueID-ul tau este [B]" + client.getUniqueIdentifier() + "[/B] si DatabaseID-ul [[B]" + client.getDatabaseId() + "[/B]]\n" +
                            "[COLOR=red]Daca vrei sa dezactivezi acest [B]welcome message[/B], foloseste [B]!client welcome[/B].[/COLOR]\n" +
                            "Daca ai nevoie de mai multe informatii despre identitatea ta, foloseste [B]!info[/B].\n\n" +
                            "Daca vrei sa iti faci un [B]canal permanent[/B], foloseste [B]!create-channel [COLOR=green]CHANNEL-NAME[/COLOR][/B].\n";
            api.sendPrivateMessage(
                    client.getId(), stringBuilder);
        }

    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
    }
}
