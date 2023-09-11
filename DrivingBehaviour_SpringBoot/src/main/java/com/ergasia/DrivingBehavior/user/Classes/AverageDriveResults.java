package com.ergasia.DrivingBehavior.user.Classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "average_drive_results")
public class AverageDriveResults {

//    @Id
//    @SequenceGenerator(
//            name = "drive_sequence",
//            sequenceName = "drive_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "drive_sequence"
//    )
//    private Long driveId;

    @Id
    private Long userId;

    private int maxSpeed;
    private int avgSpeed;
    private int maxAcceleration;
    private int minAcceleration;
    private int idleTime;
    private int numOfSessions;
    private int totalDuration;
    private int totalDistance;
    private int suddenBreaks;
    private int suddenAcceleration;
    private int suddenTurns;
    private int avgDistance;
    private int avgDuration;
    private int avgIdleTime;
    private double avgSuddenBreaks;
    private double avgSuddenAcceleration;
    private double avgSuddenTurns;
}
