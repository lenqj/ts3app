package org.example.teamspeak3app.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import org.example.teamspeak3app.service.TS3ClientService;
import org.example.teamspeak3app.service.TS3GroupService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.example.teamspeak3app.utils.UtilMethods.*;


public class TaskScheduler {
    private final TS3Api ts3Api;
    private final TS3ClientService ts3ClientService;
    private final TS3GroupService ts3GroupService;

    public TaskScheduler(TS3Api ts3Api, TS3ClientService ts3ClientService, TS3GroupService ts3GroupService) {
        this.ts3Api = ts3Api;
        this.ts3ClientService = ts3ClientService;
        this.ts3GroupService = ts3GroupService;
    }

    public void start() {
        ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService scheduler3 = Executors.newScheduledThreadPool(1);

        long initialDelay = getInitialDelay();
        scheduler1.scheduleAtFixedRate(() -> checkClientsActivity(ts3Api, ts3ClientService), 0, 30, TimeUnit.SECONDS);
        scheduler2.scheduleAtFixedRate(() -> checkClientsLevel(ts3Api, ts3ClientService, ts3GroupService), 0, 30, TimeUnit.SECONDS);
        scheduler3.scheduleAtFixedRate(() -> checkAFKClients(ts3Api), 0, 30, TimeUnit.SECONDS);

    }
}