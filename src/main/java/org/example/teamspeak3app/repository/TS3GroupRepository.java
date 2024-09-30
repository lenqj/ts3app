package org.example.teamspeak3app.repository;

import org.example.teamspeak3app.model.TS3Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TS3GroupRepository extends JpaRepository<TS3Group, Integer> {
    List<TS3Group> findAll();
    List<TS3Group> findAllByLevelTypeIsTrue();
    Optional<TS3Group> findFirstByMinimumCoinsLessThanEqualAndLevelTypeIsTrueOrderByMinimumCoinsDesc(Double minimumCoins);
}
