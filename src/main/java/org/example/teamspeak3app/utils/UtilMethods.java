package org.example.teamspeak3app.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServer;
import org.example.teamspeak3app.model.*;
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
                TS3Client ts3Client = checkClientActivity(client.getUniqueIdentifier(), ts3ClientService);
                if (ts3Client != null) {
                    ts3Api.sendPrivateMessage(client.getId(), "[PayDay] - Ai primit " + ts3Client.getLastPayday() + " coinsi la acest payday! Ai un total de " + ts3Client.getCoins() + " coinsi!");
                }
            }
        }
    }

    public static TS3Client checkClientActivity(
            String uniqueIdentifier,
            TS3ClientService ts3ClientService) {
        return ts3ClientService.updateTS3ClientMoney(uniqueIdentifier);
    }


    public static void checkClientsLevel(
            TS3Api ts3Api,
            TS3ClientService ts3ClientService,
            TS3GroupService ts3GroupService) {
        List<Client> clients = ts3Api.getClients();
        for (Client client : clients) {
            if (client.isRegularClient()) {
                Integer updatedGroupId = checkClientLevel(client.getUniqueIdentifier(), ts3ClientService);
                if (updatedGroupId != -1) {
                    List<TS3Group> allTS3LevelGroups = ts3GroupService.findAllByLevelTypeIsTrue();

                    int[] ts3ClientServerGroups = client.getServerGroups();
                    for (int ts3ClientServerGroup : ts3ClientServerGroups) {
                        if (allTS3LevelGroups.stream().anyMatch(ts3Group -> ts3Group.getId() == ts3ClientServerGroup)) {
                            ts3Api.removeClientFromServerGroup(ts3ClientServerGroup, client.getDatabaseId());
                        }
                    }

                    ts3Api.addClientToServerGroup(updatedGroupId, client.getDatabaseId());
                    ts3Api.sendPrivateMessage(client.getId(),
                            "Ai avansat la level nou!");
                }
            }
        }
    }

    private static Integer checkClientLevel(
            String uniqueIdentifier,
            TS3ClientService ts3ClientService) {
        return ts3ClientService.updateTS3ClientLevel(uniqueIdentifier);
    }

    public static void checkAFKClients(
            TS3Api ts3Api) {
        List<VirtualServer> virtualServers = ts3Api.getVirtualServers();
        for (VirtualServer virtualServer : virtualServers) {
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_CHANNEL"));
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_CLIENT"));
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_FILETRANSFER"));
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_PERMISSIONS"));
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_QUERY"));
            System.out.println(virtualServer.get("VIRTUALSERVER_LOG_SERVER"));
        }
        List<Client> clients = ts3Api.getClients();
        for (Client client : clients) {

            if (client.isRegularClient() && client.getChannelId() != 2) {
                long idleTime = client.getIdleTime();
                if(idleTime >= 600000){
                    ts3Api.moveClient(client.getId(), 2);
                    ts3Api.sendPrivateMessage(client.getId(),
                            "Ai fost AFK mai mult de " + 10 + " minute si te-am mutat pe canalul de AFK!");
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
