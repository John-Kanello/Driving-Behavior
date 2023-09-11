package com.ergasia.DrivingBehavior.user.Classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long userId;

    private String firstName;
    private String lastName;
    private String birthday;
    private String email;
    private String password;


    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

}