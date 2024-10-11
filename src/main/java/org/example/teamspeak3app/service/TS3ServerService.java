package org.example.teamspeak3app.service;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import jakarta.annotation.*;
import org.example.teamspeak3app.dto.TS3GroupDTO;
import org.example.teamspeak3app.events.*;
import org.example.teamspeak3app.model.TS3Server;
import org.example.teamspeak3app.repository.TS3ServerRepository;
import org.example.teamspeak3app.utils.TaskScheduler;
import org.example.teamspeak3app.utils.UtilMethods;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.teamspeak3app.utils.UtilMethods.setConfig;


@Service
public class TS3ServerService {
    private final TS3ServerRepository ts3ServerRepository;
    private final TS3ClientService ts3ClientService;
    private final TS3GroupService ts3GroupService;
    private final Map<String, TS3Query> activeQueries = new HashMap<>();

    public TS3ServerService(TS3ServerRepository ts3ServerRepository, TS3ClientService ts3ClientService, TS3GroupService ts3GroupService) {
        this.ts3ServerRepository = ts3ServerRepository;
        this.ts3ClientService = ts3ClientService;
        this.ts3GroupService = ts3GroupService;
    }


    public void addServer(TS3Server ts3Server) {
        TS3Query ts3Query = setConfig(ts3Server);
        activeQueries.put(ts3Server.getIpAddress(), ts3Query);

        ts3ServerRepository.save(ts3Server);
    }

    public ResponseEntity<?> startServer(Long id) {
        Optional<TS3Server> optionalTS3Server = ts3ServerRepository.findById(id);
        if (optionalTS3Server.isEmpty()) {
            return new ResponseEntity<>("Botul cu id: " + id + " nu exista!", HttpStatus.OK);
        }
        TS3Server ts3Server = optionalTS3Server.get();

        if (activeQueries.containsKey(ts3Server.getIpAddress())) {
            TS3Query ts3Query = activeQueries.get(ts3Server.getIpAddress());
            if (ts3Query.isConnected()) {
                TS3Api ts3Api = ts3Query.getApi();
                ts3Api.login(ts3Server.getUsername(), ts3Server.getPassword());
                ts3Api.selectVirtualServerByPort(ts3Server.getPort());
                ts3Api.setNickname(ts3Server.getBotName());
                ts3Api.registerAllEvents();

                ts3Api.addTS3Listeners(
                        new PrivateMessageEvent(ts3Api, ts3ClientService),
                        new WelcomeMessageEvent(ts3Api),
                        new ClientPaydayEvent(ts3Api, ts3ClientService),
                        new ChannelDeletionEvent(ts3Api, ts3ClientService),
                        new ClientKickEvent(ts3Api, ts3ClientService)
                );

                ts3Api.getServerGroups().forEach(serverGroup -> ts3GroupService.addTS3Group(
                        TS3GroupDTO.builder()
                                .name(serverGroup.getName())
                                .id(serverGroup.getId())
                                .build()));

                new TaskScheduler(ts3Api, ts3ClientService, ts3GroupService).start();
                return new ResponseEntity<>("Botul a fost pornit!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Nu exista conexiune la server!", HttpStatus.BAD_GATEWAY);
            }
        }
        return new ResponseEntity<>("Botul cu id: " + id + " nu exista!", HttpStatus.OK);
    }


    public ResponseEntity<?> stopServer(Long id) {
        Optional<TS3Server> optionalTS3Server = ts3ServerRepository.findById(id);
        if (optionalTS3Server.isEmpty()) {
            return new ResponseEntity<>("Botul cu id: " + id + " nu exista!", HttpStatus.OK);
        }
        TS3Server ts3Server = optionalTS3Server.get();

        if (activeQueries.containsKey(ts3Server.getIpAddress())) {
            TS3Query ts3Query = activeQueries.get(ts3Server.getIpAddress());
            if (ts3Query.isConnected()) {
                TS3Api ts3Api = ts3Query.getApi();
                ts3Api.logout();
                return new ResponseEntity<>("Botul a fost oprit!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Nu exista conexiune la server!", HttpStatus.BAD_GATEWAY);
            }
        }
        return new ResponseEntity<>("Botul cu id: " + id + " nu exista!", HttpStatus.OK);
    }


    @PostConstruct
    public void initializeConnections() {
        List<TS3Server> ts3ServerList = ts3ServerRepository.findAll();

        for (TS3Server ts3Server : ts3ServerList) {
            TS3Query ts3Query = UtilMethods.setConfig(ts3Server);
            activeQueries.put(ts3Server.getIpAddress(), ts3Query);
        }
    }

}
