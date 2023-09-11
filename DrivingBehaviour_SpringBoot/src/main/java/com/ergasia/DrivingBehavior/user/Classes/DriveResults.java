package com.ergasia.DrivingBehavior.user.Classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "drive_table")
public class DriveResults {

    @Id
    @SequenceGenerator(
            name = "drive_sequence",
            sequenceName = "drive_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "drive_sequence"
    )
    private Long driveId;

    private Long userId;
    private int maxSpeed;
    private int avgSpeed;
    private String startHour;
    private String endHour;
    private String date;
    private String duration;
    private int distance;
    private int maxAcceleration;
    private int minAcceleration;
    private String idleTime;
    private int suddenBreaks;
    private int suddenAccelerations;
    private int suddenTurns;
    
}
