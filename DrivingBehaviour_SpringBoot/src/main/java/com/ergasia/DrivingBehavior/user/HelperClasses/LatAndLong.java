package com.ergasia.DrivingBehavior.user.HelperClasses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "dangerous_locations")
public class LatAndLong {

    @Id
    @SequenceGenerator(
            name = "location_sequence",
            sequenceName = "location_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "location_sequence"
    )
    private Long id;

    private double latitude;
    private double longitude;
    private String type;

    public LatAndLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
