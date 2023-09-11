package com.ergasia.DrivingBehavior.user.Controller;

import com.ergasia.DrivingBehavior.user.Classes.AverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.DriveResults;
import com.ergasia.DrivingBehavior.user.Classes.GlobalAverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.User;
import com.ergasia.DrivingBehavior.user.HelperClasses.LatAndLong;
import com.ergasia.DrivingBehavior.user.HelperClasses.UserStatus;
import com.ergasia.DrivingBehavior.user.Service.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserServiceImplementation userService;

    @PostMapping("/users")
        public User saveUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/users/drive")
    public UserStatus saveDriveDetais(@Valid @RequestBody DriveResults driveResults) {
        return userService.saveDriveDetails(driveResults);
    }

    @PostMapping("users/averageDriveResults")
    public void updateDriveResults(@Valid @RequestBody AverageDriveResults averageDriveResults){
        userService.updateDriveDetails(averageDriveResults);
    }

    @GetMapping("/users")
    public List<User> fetchUserList()
    {
        return userService.fetchUserList();
    }

    @GetMapping("/users/{id}")
    public User fetchUserById(@PathVariable("id") Long userId)
    {
        return userService.fetchUserById(userId);
    }

    @PostMapping("/users/login")
    public User fetchUserByEmail(@Valid @RequestBody User user)
    {
        return userService.fetchUserByEmail(user);
    }

    @DeleteMapping("/users/{id}")
    public UserStatus deleteUserById(@PathVariable("id") Long userId)
    {
        return userService.deleteUserById(userId);
    }

    @PutMapping("/users")
    public UserStatus updateUser(@RequestBody User user)
    {
        return userService.updateUser(user);
    }

    @PostMapping("users/getInformation")
    public List<DriveResults> getUserInformation(@Valid @RequestBody int id){return userService.getUserInformation(id);}

    @PostMapping("users/getAverageInformation")
    public AverageDriveResults getUserAverageInformation(@Valid @RequestBody int id){return userService.getAverageUserInformation(id);}

    @GetMapping("users/getGlobalAverageInformation")
    public GlobalAverageDriveResults getGlobalAverageInformation(){return userService.getGlobalAverageInformation();}

    @PostMapping("dangerousLocations")
    public void saveDangerousLocation(@Valid @RequestBody LatAndLong latAndLong){userService.saveDangerousLocation(latAndLong);}

}
