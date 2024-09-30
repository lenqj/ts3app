package org.example.teamspeak3app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TS3Client {
    @Id
    private String id;

    @Column(unique = true)
    private String username;

    private String password;

    private Double coins;

    private LocalDateTime lastPaydayDate;

    private Double lastPayday;

    private LocalDateTime lastActionTime;

    private Integer noOfActions;

    @ManyToOne
    private TS3Group ts3LevelGroup;
}
