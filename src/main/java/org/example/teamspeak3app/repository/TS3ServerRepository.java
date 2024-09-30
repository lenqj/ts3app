package org.example.teamspeak3app.repository;

import org.example.teamspeak3app.model.TS3Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TS3ServerRepository extends JpaRepository<TS3Server, Long> {
}
