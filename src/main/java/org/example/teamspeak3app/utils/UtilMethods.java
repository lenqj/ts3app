package org.example.teamspeak3app.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.example.teamspeak3app.model.TS3Client;
import org.example.teamspeak3app.model.TS3Group;
import org.example.teamspeak3app.model.TS3Server;
import org.example.teamspeak3app.service.TS3ClientService;
import org.example.teamspeak3app.service.TS3GroupService;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class UtilMethods {
    public static TS3Query setConfig(
            TS3Server ts3Server) {
        final TS3Config config = new TS3Config();

        config.setHost(ts3Server.getIpAddress());
        config.setEnableCommunicationsLogging(true);
        config.setLoginCredentials(ts3Server.getUsername(), ts3Server.getPassword());
        config.setQueryPort(ts3Server.getQueryPort());

        TS3Query ts3Query = new TS3Query(config);
        ts3Query.connect();

        return ts3Query;
    }

    public static void checkClientsActivity(
            TS3Api ts3Api,
            TS3ClientService ts3ClientService) {
        List<Client> clients = ts3Api.getClients();
        for (Client client : clients) {
            if (client.isRegularClient()) {
                TS3Client ts3Client = ts3ClientService.updateTS3ClientMoney(client.getUniqueIdentifier());
                if (ts3Client != null && !ts3Client.isIgnorePaydayNotifications()) {
                    Double lastPaydayCoins = ts3Client.getLastPayday();
                    Double totalCoins = ts3Client.getCoins();

                    String paydayMessage = "[B][PayDay][/B] - [color=green]You've received " + lastPaydayCoins + " coins[/color] during this payday! " +
                            "You now have a total of [color=yellow]" + totalCoins + " coins[/color]!";

                    ts3Api.sendPrivateMessage(client.getId(), paydayMessage);
                }
            }
        }
    }

    public static void checkClientsLevel(
            TS3Api ts3Api,
            TS3ClientService ts3ClientService,
            TS3GroupService ts3GroupService) {
        List<Client> clients = ts3Api.getClients();
        for (Client client : clients) {
            if (client.isRegularClient()) {
                TS3Client ts3Client = ts3ClientService.updateTS3ClientLevel(client.getUniqueIdentifier());
                if (ts3Client != null && ts3Client.getTs3LevelGroup() != null) {
                    List<TS3Group> allTS3LevelGroups = ts3GroupService.findAllByLevelTypeIsTrue();

                    int[] ts3ClientServerGroups = client.getServerGroups();
                    for (int ts3ClientServerGroup : ts3ClientServerGroups) {
                        if (allTS3LevelGroups.stream().anyMatch(ts3Group -> ts3Group.getId() == ts3ClientServerGroup)) {
                            ts3Api.removeClientFromServerGroup(ts3ClientServerGroup, client.getDatabaseId());
                        }
                    }
                    TS3Group ts3Group = ts3Client.getTs3LevelGroup();

                    ts3Api.addClientToServerGroup(ts3Group.getId(), client.getDatabaseId());

                    if (!ts3Client.isIgnorePaydayNotifications()) {
                        String levelUpMessage = "[B][Level Up][/B] - [color=green]Congratulations![/color] " +
                                "You have advanced to [color=yellow] " + ts3Group.getName() + "[/color]!";
                        ts3Api.sendPrivateMessage(client.getId(), levelUpMessage);
                    }

                }
            }
        }
    }

    public static void checkAFKClients(
            TS3Api ts3Api,
            TS3ClientService ts3ClientService) {
        List<Client> clients = ts3Api.getClients();
        for (Client client : clients) {
            if (client.isRegularClient() && client.getChannelId() != 2) {
                TS3Client ts3Client = ts3ClientService.findTS3ClientById(client.getUniqueIdentifier());
                long idleTime = client.getIdleTime();
                if (idleTime >= 600000 && ts3Client != null && !ts3Client.isIgnoreAutoAFKMove()) {
                    ts3Api.moveClient(client.getId(), 2);

                    int afkMinutes = 10;
                    String afkMessage = "[B][AFK Notice][/B] - [color=red]You have been AFK for more than " + afkMinutes + " minutes[/color], " +
                            "so we've moved you to the AFK channel!";
                    ts3Api.sendPrivateMessage(client.getId(), afkMessage);
                }
            }
        }
    }


    public static Long getInitialDelay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - System.currentTimeMillis() > 0 ? calendar.getTimeInMillis() - System.currentTimeMillis() : 0;
    }
}
