package com.ergasia.DrivingBehavior.user.Service;

import com.ergasia.DrivingBehavior.user.Classes.AverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.DriveResults;
import com.ergasia.DrivingBehavior.user.Classes.GlobalAverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.User;
import com.ergasia.DrivingBehavior.user.HelperClasses.LatAndLong;
import com.ergasia.DrivingBehavior.user.HelperClasses.UserStatus;

import java.util.List;

public interface UserSevice {

    public User saveUser(User user);

    public List<User> fetchUserList();

    public User fetchUserByEmail(User user);

    public User fetchUserById(Long userId) throws IllegalStateException;

    public UserStatus deleteUserById(Long userId);

    public UserStatus updateUser(User user);

    public UserStatus saveDriveDetails(DriveResults driveResults);

    public void updateDriveDetails(AverageDriveResults averageDriveResults);

    public List<DriveResults> getUserInformation(int id);

    public AverageDriveResults getAverageUserInformation(int id);

    public GlobalAverageDriveResults getGlobalAverageInformation();

    public void saveDangerousLocation(LatAndLong latAndLong);

}
