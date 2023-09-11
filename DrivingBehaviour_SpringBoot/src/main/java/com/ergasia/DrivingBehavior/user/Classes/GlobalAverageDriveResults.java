package com.ergasia.DrivingBehavior.user.Classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "global_drive_results")
public class GlobalAverageDriveResults {

    @Id
//    @SequenceGenerator(
//            name = "user_sequence",
//            sequenceName = "user_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "user_sequence"
//    )
    private Long global_id;

    private int avgSpeed;
    private int numOfSessions;
    private int avgDistance;
    private int avgDuration;
    private int avgIdleTime;
    private double avgSuddenBreaks;
    private double avgSuddenAcceleration;
    private double avgSuddenTurns;

    public GlobalAverageDriveResults(int avgSpeed, int avgDistance, int avgDuration,
                                     int avgIdleTime, double avgSuddenBreaks, double avgSuddenAcceleration, double avgSuddenTurns) {
        this.avgSpeed = avgSpeed;
        this.avgDistance = avgDistance;
        this.avgDuration = avgDuration;
        this.avgIdleTime = avgIdleTime;
        this.avgSuddenBreaks = avgSuddenBreaks;
        this.avgSuddenAcceleration = avgSuddenAcceleration;
        this.avgSuddenTurns = avgSuddenTurns;
    }
}
