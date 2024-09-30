package org.example.teamspeak3app.service;

import org.example.teamspeak3app.dto.TS3ClientDTO;
import org.example.teamspeak3app.dto.builder.TS3ClientBuilder;
import org.example.teamspeak3app.model.TS3Client;
import org.example.teamspeak3app.model.TS3Group;
import org.example.teamspeak3app.repository.TS3ClientRepository;
import org.example.teamspeak3app.repository.TS3GroupRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TS3ClientService {
    private final TS3ClientRepository ts3ClientRepository;
    private final TS3GroupRepository ts3GroupRepository;

    public TS3ClientService(TS3ClientRepository ts3ClientRepository, TS3GroupRepository ts3GroupRepository) {
        this.ts3ClientRepository = ts3ClientRepository;
        this.ts3GroupRepository = ts3GroupRepository;
    }

    public TS3Client addTS3Client(TS3ClientDTO ts3ClientDTO) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(ts3ClientDTO.getId());
        if (optionalTS3Client.isPresent()) {
            return null;
        }

        TS3Client ts3Client = TS3ClientBuilder.generateEntity(ts3ClientDTO);
        return ts3ClientRepository.save(ts3Client);
    }

    public TS3Client updateTS3ClientMoney(String uniqueIdentifier) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(uniqueIdentifier);
        if (optionalTS3Client.isEmpty()) {
            return null;
        }

        TS3Client ts3Client = optionalTS3Client.get();

        LocalDateTime lastActivityTime = ts3Client.getLastPaydayDate();
        if (lastActivityTime != null) {
            Duration timeSinceLastActivity = Duration.between(lastActivityTime, LocalDateTime.now());

            if (timeSinceLastActivity.toSeconds() >= 30) {
                TS3Group clientTS3CurrentLevel = ts3Client.getTs3LevelGroup();
                Double maximumBonus;
                if (clientTS3CurrentLevel != null) {
                    maximumBonus = clientTS3CurrentLevel.getMaximumBonus();
                } else {
                    maximumBonus = 0.0;
                }

                Double payday = BigDecimal.valueOf(35 + maximumBonus * Math.random()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                Double totalCoins = BigDecimal.valueOf(ts3Client.getCoins() + payday).setScale(2, RoundingMode.HALF_UP).doubleValue();

                ts3Client.setLastPayday(payday);
                ts3Client.setLastPaydayDate(LocalDateTime.now());
                ts3Client.setCoins(totalCoins);

                ts3Client = ts3ClientRepository.save(ts3Client);
                return ts3Client;
            }
        } else {
            ts3Client.setLastPaydayDate(LocalDateTime.now());
            ts3ClientRepository.save(ts3Client);
            return null;
        }

        return null;
    }

    public Integer updateTS3ClientLevel(String uniqueIdentifier) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(uniqueIdentifier);
        if (optionalTS3Client.isEmpty()) {
            return -1;
        }

        TS3Client ts3Client = optionalTS3Client.get();

        Optional<TS3Group> optionalTS3Group = ts3GroupRepository.findFirstByMinimumCoinsLessThanEqualAndLevelTypeIsTrueOrderByMinimumCoinsDesc(ts3Client.getCoins());
        if (optionalTS3Group.isEmpty()) {
            return -1;
        }
        TS3Group ts3Group = optionalTS3Group.get();

        if (ts3Client.getTs3LevelGroup() != null &&
                ts3Group.getId() == ts3Client.getTs3LevelGroup().getId()) {
            return -1;
        }

        ts3Client.setTs3LevelGroup(ts3Group);
        ts3Client = ts3ClientRepository.save(ts3Client);

        return ts3Client.getTs3LevelGroup().getId();
    }

    public void updateTS3ClientLastPayDay(String uniqueIdentifier) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(uniqueIdentifier);
        if (optionalTS3Client.isEmpty()) {
            return;
        }

        TS3Client ts3Client = optionalTS3Client.get();

        ts3Client.setLastPaydayDate(LocalDateTime.now());
        ts3ClientRepository.save(ts3Client);
    }

    public TS3Client updateTS3ClientOnDeleteChannel(String uniqueIdentifier) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(uniqueIdentifier);
        if (optionalTS3Client.isEmpty()) {
            return null;
        }

        TS3Client ts3Client = optionalTS3Client.get();

        if (ts3Client.getLastActionTime() == null) {
            ts3Client.setLastActionTime(LocalDateTime.now());
            ts3Client.setNoOfActions(1);
        } else {
            Duration duration = Duration.between(ts3Client.getLastActionTime(), LocalDateTime.now());
            if (duration.getSeconds() < 10) {
                ts3Client.setNoOfActions(ts3Client.getNoOfActions() + 1);
                ts3Client.setLastActionTime(LocalDateTime.now());
            } else {
                ts3Client.setNoOfActions(null);
                ts3Client.setLastActionTime(null);
            }
        }

        return ts3ClientRepository.save(ts3Client);
    }

    public TS3Client updateTS3Client(TS3Client ts3Client) {
        Optional<TS3Client> optionalTS3Client = ts3ClientRepository.findById(ts3Client.getId());
        if (optionalTS3Client.isEmpty()) {
            return null;
        }

        return ts3ClientRepository.save(ts3Client);
    }

}
