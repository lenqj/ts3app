package org.example.teamspeak3app.controller;

import org.example.teamspeak3app.model.TS3Server;
import org.example.teamspeak3app.service.TS3ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
public class TS3ServerController {
    private final TS3ServerService ts3ServerService;

    public TS3ServerController(TS3ServerService ts3ServerService) {
        this.ts3ServerService = ts3ServerService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private void addServer(@RequestBody TS3Server ts3Server) {
        ts3ServerService.addServer(ts3Server);
    }

    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    private ResponseEntity<?> startServer(@PathVariable Long id) {
        return ts3ServerService.startServer(id);
    }

    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    private ResponseEntity<?> stopServer(@PathVariable Long id) {
        return ts3ServerService.stopServer(id);
    }

}
