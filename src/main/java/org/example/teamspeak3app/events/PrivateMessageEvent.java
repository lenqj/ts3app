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
        if (client.isRegularClient() &&
                client.getId() != api.whoAmI().getId() &&
                e.getTargetMode() == TextMessageTargetMode.CLIENT
        ) {
            String message = e.getMessage();
            StringBuilder notificationMessageForUser = new StringBuilder("Invalid command, use !help to get list of all available commands!");
            AbstractMap.SimpleEntry<Integer, String> commandAndArguments = MessageHelper.handleMessage(message);
            if (commandAndArguments != null) {
                switch (commandAndArguments.getKey()) {
                    case 1: {
                        String channelName = commandAndArguments.getValue();
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
                                        new StringBuilder("[B][SUCCESS][/B] - [color=green]You have successfully created the channel named: " + channelName + "![/color]");
                            } else {
                                notificationMessageForUser =
                                        new StringBuilder("[B][ERROR][/B] - [color=red]The channel with the name " + channelName + " already exists![/color]");
                            }
                        } else {
                            notificationMessageForUser =
                                    new StringBuilder("[B][ERROR][/B] - [color=red]Please use the correct format: !create-channel CHANNEL-NAME.[/color]");
                        }
                    }
                    break;
                    case 2: {
                        String[] arguments = commandAndArguments.getValue().split(" ");
                        if (arguments.length == 2) {
                            String username = arguments[0];
                            String password = arguments[1];
                            TS3ClientDTO ts3ClientDTO = TS3ClientDTO.builder()
                                    .id(client.getUniqueIdentifier())
                                    .username(username)
                                    .password(password)
                                    .build();
                            TS3Client ts3Client = ts3ClientService.addTS3Client(ts3ClientDTO);
                            if (ts3Client != null) {
                                notificationMessageForUser =
                                        new StringBuilder("[B][SUCCESS][/B] - [color=green]" + username + ", you have successfully registered![/color]");
                            } else {
                                notificationMessageForUser =
                                        new StringBuilder("[B][ERROR][/B] - [color=red]" + username + " is already registered with us![/color]");
                            }
                        } else {
                            notificationMessageForUser =
                                    new StringBuilder("[B][ERROR][/B] - [color=red]Incorrect syntax, please use: !register username password.[/color]");
                        }
                    }
                    break;
                    case 3: {
                        notificationMessageForUser = new StringBuilder("Available commands for [B]!client[/B]: \n");

                        String[] clientCommands = {
                                "[B]!client welcome[/B] - [color=green]enable[/color]/[color=red]disable[/color] the welcome message when you connect to the server.",
                                "[B]!client automove[/B] - [color=green]enable[/color]/[color=red]disable[/color] the automove to the channel you disconnected.",
                                "[B]!client autoafk[/B] - [color=green]enable[/color]/[color=red]disable[/color] automatic move to AFK channel, if you are idle for few minutes.",
                                "[B]!client payday[/B] - [color=green]enable[/color]/[color=red]disable[/color] the messages related to changes of your balance.",
                                "[B]!client showrank/showlevel[/B] - [color=green]enable[/color]/[color=red]disable[/color] setting the server groups for Level 5, 10 etc."
                        };

                        for (String command : clientCommands) {
                            notificationMessageForUser.append(command).append("\n");
                        }

                        if (commandAndArguments.getValue() != null) {
                            String[] arguments = commandAndArguments.getValue().split(" ");
                            if (arguments.length > 0) {
                                if (arguments.length == 1) {
                                    switch (arguments[0]) {
                                        case "info": {
                                            notificationMessageForUser =
                                                    new StringBuilder("You are, " + client.getNickname());
                                        }
                                        break;
                                        case "payday": {
                                            TS3Client ts3Client = ts3ClientService.findTS3ClientById(client.getUniqueIdentifier());
                                            if (ts3Client != null) {
                                                ts3Client.setIgnorePaydayNotifications(!ts3Client.isIgnorePaydayNotifications());
                                                ts3ClientService.updateTS3Client(ts3Client);
                                            }
                                        }
                                        break;
                                        case "welcome": {
                                            TS3Client ts3Client = ts3ClientService.findTS3ClientById(client.getUniqueIdentifier());
                                            if (ts3Client != null) {
                                                ts3Client.setIgnoreWelcomeNotifications(!ts3Client.isIgnoreWelcomeNotifications());
                                                ts3ClientService.updateTS3Client(ts3Client);
                                            }
                                        }
                                        break;
                                        case "auto-afk": {
                                            TS3Client ts3Client = ts3ClientService.findTS3ClientById(client.getUniqueIdentifier());
                                            if (ts3Client != null) {
                                                ts3Client.setIgnoreAutoAFKMove(!ts3Client.isIgnoreAutoAFKMove());
                                                ts3ClientService.updateTS3Client(ts3Client);
                                            }
                                        }
                                        break;
                                        case "auto-move": {
                                            TS3Client ts3Client = ts3ClientService.findTS3ClientById(client.getUniqueIdentifier());
                                            if (ts3Client != null) {
                                                ts3Client.setIgnoreAutoMoveToHomeChannel(!ts3Client.isIgnoreAutoMoveToHomeChannel());
                                                ts3ClientService.updateTS3Client(ts3Client);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                    case 4: {
                        notificationMessageForUser =
                                new StringBuilder("Available commands: !");

                        String[] commands = {
                                "[B]!help[/B] - [color=blue]Displays the list of commands![/color]",
                                "[B]!client[/B] - [color=blue]Displays the list of client commands![/color]",
                                "[B]!create-channel channel-name[/B] - [color=blue]Creates a channel.[/color]"
                        };

                        for (String command : commands) {
                            notificationMessageForUser.append(command).append("\n");
                        }
                    }
                    break;
                }
            }
            api.sendPrivateMessage(
                    client.getId(), String.valueOf(notificationMessageForUser));

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
    }
}