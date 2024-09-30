package org.example.teamspeak3app.repository;


import org.example.teamspeak3app.model.TS3Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TS3ClientRepository extends JpaRepository<TS3Client, String> {
}
