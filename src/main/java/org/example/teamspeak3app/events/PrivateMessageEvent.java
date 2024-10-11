package org.example.teamspeak3app.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.example.teamspeak3app.dto.TS3ClientDTO;
import org.example.teamspeak3app.model.TS3Client;
import org.example.teamspeak3app.service.TS3ClientService;
import org.example.teamspeak3app.utils.MessageHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class PrivateMessageEvent implements TS3Listener {
    private final TS3Api api;
    private final TS3ClientService ts3ClientService;

    public PrivateMessageEvent(TS3Api api, TS3ClientService ts3ClientService) {
        this.api = api;
        this.ts3ClientService = ts3ClientService;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        Client client = api.getClientInfo(e.getInvokerId());
        if (client.isRegularClient() && client.getId() != api.whoAmI().getId()) {
            if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
                String message = e.getMessage();

                AbstractMap.SimpleEntry<Integer, String> commandAndArguments = MessageHelper.handleMessage(message);
                if (commandAndArguments != null) {
                    switch (commandAndArguments.getKey()) {
                        case 1: {
                            String channelName = commandAndArguments.getValue();
                            String notificationMessageForUser;
                            if (channelName != null) {
                                final Map<ChannelProperty, String> properties = new HashMap<>();
                                properties.put(ChannelProperty.CPID, String.valueOf(73));
                                properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
                                properties.put(ChannelProperty.CHANNEL_TOPIC, "Created by " +
                                        api.whoAmI().getNickname() +
                                        " for " +
                                        client.getUniqueIdentifier() +
                                        " on " +
                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(LocalDateTime.now()));

                                if (api.getChannelByNameExact(channelName, true) == null) {
                                    int channelId = api.createChannel(channelName, properties);
                                    api.moveClient(client.getId(), channelId);
                                    api.setClientChannelGroup(5, channelId, client.getDatabaseId());
                                    notificationMessageForUser =
                                            "Ai creat cu succes canalul cu numele: " + channelName + "!";
                                } else {
                                    notificationMessageForUser =
                                            "Canalul cu numele " + channelName + " exista deja!";
                                }
                            } else {
                                notificationMessageForUser = "[EROARE] - Foloseste !create-channel NUME-CANAL.";
                            }
                            api.sendPrivateMessage(
                                    client.getId(), notificationMessageForUser);
                        }
                        break;
                        case 2: {
                            String[] arguments = commandAndArguments.getValue().split(" ");
                            String notificationMessageForUser;
                            if (arguments.length == 2) {
                                String username = arguments[0];
                                String password = arguments[1];
                                TS3ClientDTO ts3ClientDTO = TS3ClientDTO.builder()
                                        .id(client.getUniqueIdentifier())
                                        .username(username)
                                        .password(password)
                                        .coins(0.0)
                                        .build();
                                TS3Client ts3Client = ts3ClientService.addTS3Client(ts3ClientDTO);
                                if (ts3Client != null) {
                                    notificationMessageForUser = username + " te-ai inregistrat cu succes!";
                                } else {
                                    notificationMessageForUser = username + " este deja inregistrat la noi!";
                                }
                            } else {
                                notificationMessageForUser = "[EROARE] - Sintaxa este gresita, foloseste !register nume parola.";
                            }
                            api.sendPrivateMessage(
                                    client.getId(), notificationMessageForUser);
                        }
                        break;
                        case 3: {
                            String notificationMessageForUser =
                                    "info!";
                            api.sendPrivateMessage(
                                    client.getId(), notificationMessageForUser);
                        }
                        break;
                        default: {
                            String notificationMessageForUser =
                                    "invalid";
                            api.sendPrivateMessage(
                                    client.getId(), notificationMessageForUser);
                        }
                    }
                } else {
                    String notificationMessageForUser =
                            "invalid";
                    api.sendPrivateMessage(
                            client.getId(), notificationMessageForUser);
                }
            }
        }
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
    }
}