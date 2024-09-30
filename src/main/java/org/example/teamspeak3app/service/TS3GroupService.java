package org.example.teamspeak3app.service;

import org.example.teamspeak3app.dto.TS3GroupDTO;
import org.example.teamspeak3app.model.TS3Group;
import org.example.teamspeak3app.repository.TS3GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TS3GroupService {
    private final TS3GroupRepository ts3GroupRepository;

    public TS3GroupService(TS3GroupRepository ts3GroupRepository) {
        this.ts3GroupRepository = ts3GroupRepository;
    }

    public List<TS3Group> findAllByLevelTypeIsTrue() {
        return ts3GroupRepository.findAllByLevelTypeIsTrue();
    }

    public void addTS3Group(TS3GroupDTO ts3GroupDTO) {
        Optional<TS3Group> ts3GroupOptional = ts3GroupRepository.findById(ts3GroupDTO.getId());
        if (ts3GroupOptional.isPresent()) {
            TS3Group ts3GroupFromDB = ts3GroupOptional.get();
            ts3GroupFromDB.setName(ts3GroupDTO.getName());

            ts3GroupRepository.save(ts3GroupFromDB);
            return;
        }

        TS3Group ts3Group = TS3Group.builder()
                .id(ts3GroupDTO.getId())
                .name(ts3GroupDTO.getName())
                .levelType(ts3GroupDTO.isLevelType())
                .minimumCoins(ts3GroupDTO.getMinimumCoins())
                .maximumBonus(ts3GroupDTO.getMaximumBonus())
                .build();

        ts3GroupRepository.save(ts3Group);
    }
}
