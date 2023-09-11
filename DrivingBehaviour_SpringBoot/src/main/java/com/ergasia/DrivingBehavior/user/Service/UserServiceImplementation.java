package com.ergasia.DrivingBehavior.user.Service;

import com.ergasia.DrivingBehavior.user.Classes.AverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.DriveResults;
import com.ergasia.DrivingBehavior.user.Classes.GlobalAverageDriveResults;
import com.ergasia.DrivingBehavior.user.Classes.User;
import com.ergasia.DrivingBehavior.user.HelperClasses.LatAndLong;
import com.ergasia.DrivingBehavior.user.HelperClasses.UserStatus;
import com.ergasia.DrivingBehavior.user.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserSevice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriveResultsRepository driveResultsRepository;

    @Autowired
    private AverageDriveResultsRepository averageDriveResultsRepository;

    @Autowired
    private GlobalAverageDriveResultsRepository globalAverageDriveResultsRepository;

    @Autowired
    private DangerousLocationsRepository dangerousLocationsRepository;

    @Override
    public User saveUser(User user) {

        List<User> users = userRepository.findAll();

        for(User currUser : users)
        {
            if(currUser == user) {
                System.out.println("User Already exists!");
                return null;
            }
            else if(currUser.getEmail().equals(user.getEmail()))
            {
                System.out.println("A user with email already exists");
                return null;
            }
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> fetchUserList() {
        return userRepository.findAll();
    }

    @Override
    public User fetchUserByEmail(User user) {

        List<User> users = userRepository.findAll();

        for(User currUser : users)
        {
            if(currUser.getEmail().equals(user.getEmail()) && currUser.getPassword().equals(user.getPassword()))
            {
                return currUser;
            }
        }

        return null;
    }

    @Override   
    public User fetchUserById(Long userId) throws IllegalStateException {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty())
        {
            System.out.println("User with id: " + userId + " does not exist");
            return new User();
        }
        return user.get();
    }

    @Override
    public UserStatus deleteUserById(Long userId) {

        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty())
        {
            System.out.println("User does not exist");
            return UserStatus.FAILURE;
        }
        else
        {
            userRepository.deleteById(userId);
            return UserStatus.SUCCESS;
        }
    }

    @Override
    public UserStatus updateUser(User user) {

        Optional<User> currUser = userRepository.findById(user.getUserId());

        if(currUser.isEmpty())
        {
            System.out.println("User does not exist");
            return UserStatus.FAILURE;
        }

        if(Objects.nonNull(user.getEmail()) && !"".equalsIgnoreCase(user.getEmail()))
        {
            currUser.get().setEmail(user.getEmail());
        }

        if(Objects.nonNull(user.getFirstName()) && ! "".equalsIgnoreCase(user.getFirstName()))
        {
            currUser.get().setFirstName(user.getFirstName());
        }

        if(Objects.nonNull(user.getLastName()) && ! "".equalsIgnoreCase(user.getLastName()))
        {
            currUser.get().setLastName(user.getLastName());
        }

        if(Objects.nonNull(user.getBirthday()) && !"".equalsIgnoreCase(user.getBirthday()))
        {
            currUser.get().setBirthday(user.getBirthday());
        }

        if(Objects.nonNull(user.getPassword()) && !"".equalsIgnoreCase(user.getPassword()))
        {
            currUser.get().setPassword(user.getPassword());
        }

        userRepository.save(currUser.get());
        return UserStatus.SUCCESS;
    }

    @Override
    public UserStatus saveDriveDetails(DriveResults driveResults) {

        driveResultsRepository.save(driveResults);

        return UserStatus.SUCCESS;
    }

    @Override
    public void updateDriveDetails(AverageDriveResults averageDriveResults) {

        List<GlobalAverageDriveResults> list = globalAverageDriveResultsRepository.findAll();
        GlobalAverageDriveResults globalResults;
        if (list.isEmpty()) {
            globalResults = new GlobalAverageDriveResults();
        } else
        {
            globalResults = list.get(0);
        }

        setGlobalAvgDriveResults(globalResults, averageDriveResults);

        Optional<AverageDriveResults> optional = averageDriveResultsRepository.findById(averageDriveResults.getUserId());
        AverageDriveResults results;
        if(optional.isEmpty())
        {
            results = new AverageDriveResults();
        }
        else
        {
            results = optional.get();
        }
        setAverageDriveResultsObject(results, averageDriveResults);


        averageDriveResultsRepository.save(results);
    }

    @Override
    public List<DriveResults> getUserInformation(int id) {
        List<DriveResults> driveResults = driveResultsRepository.findAll();

        for(int i=0; i<driveResults.size(); i++)
        {
            if(driveResults.get(i).getUserId()!=id)
            {
                driveResults.remove(i);
                i--;
            }
        }

        return driveResults;
    }

    @Override
    public AverageDriveResults getAverageUserInformation(int id) {
        Optional<AverageDriveResults> averageDriveResults =  averageDriveResultsRepository.findById((long) id);
        return averageDriveResults.get();
    }

    @Override
    public GlobalAverageDriveResults getGlobalAverageInformation() {

        List<GlobalAverageDriveResults> globalAverageDriveResults = globalAverageDriveResultsRepository.findAll();

        return new GlobalAverageDriveResults(
                globalAverageDriveResults.get(0).getAvgSpeed(),
                globalAverageDriveResults.get(0).getAvgDistance(),
                globalAverageDriveResults.get(0).getAvgDuration(),
                globalAverageDriveResults.get(0).getAvgIdleTime(),
                globalAverageDriveResults.get(0).getAvgSuddenBreaks(),
                globalAverageDriveResults.get(0).getAvgSuddenAcceleration(),
                globalAverageDriveResults.get(0).getAvgSuddenTurns()
        );
    }

    @Override
    public void saveDangerousLocation(LatAndLong latAndLong) {
        dangerousLocationsRepository.save(latAndLong);
    }

    private void setGlobalAvgDriveResults(GlobalAverageDriveResults results, AverageDriveResults averageDriveResults)
    {
        int avgSpeed = (results.getAvgSpeed()*results.getNumOfSessions()+
                averageDriveResults.getAvgSpeed())/(results.getNumOfSessions()+1);
        results.setAvgSpeed(avgSpeed);

        int avgDistance = (results.getAvgDistance()*results.getNumOfSessions()+
                averageDriveResults.getAvgDistance())
                /(results.getNumOfSessions()+1);

        results.setAvgDistance(avgDistance);

        int avgDuration = ((results.getAvgDuration()*results.getNumOfSessions())+averageDriveResults.getTotalDuration())/(results.getNumOfSessions()+1);
        results.setAvgDuration(avgDuration);

        int idlePercent = ((averageDriveResults.getIdleTime()/averageDriveResults.getTotalDuration())*100);

        int avgIdle = ((results.getAvgIdleTime()*results.getNumOfSessions()+idlePercent)/(results.getNumOfSessions()+1));
        results.setAvgIdleTime(avgIdle);

        if(averageDriveResults.getTotalDistance() == 0)
        {
            results.setAvgSuddenBreaks(0.0);
            results.setAvgSuddenAcceleration(0.0);
            results.setAvgSuddenTurns(0.0);
        }
        else
        {
            DecimalFormat df = new DecimalFormat("#.##");
            double breaksResult = (double) (averageDriveResults.getSuddenBreaks()*1000) / (double) averageDriveResults.getTotalDistance();
            double accelerationsResult = (double) (averageDriveResults.getSuddenAcceleration()*1000) / (double) averageDriveResults.getTotalDistance();
            double turnsResult = (double) (averageDriveResults.getSuddenTurns()*1000) / (double) averageDriveResults.getTotalDistance();

            results.setAvgSuddenBreaks(Double.parseDouble(df.format(results.getAvgSuddenBreaks()*results.getNumOfSessions()+breaksResult)));
            results.setAvgSuddenAcceleration(Double.parseDouble(df.format(results.getAvgSuddenAcceleration()*results.getNumOfSessions()+accelerationsResult)));
            results.setAvgSuddenTurns(Double.parseDouble(df.format(results.getAvgSuddenTurns()*results.getNumOfSessions()+turnsResult)));
        }

        int numOfSessions = results.getNumOfSessions();
        results.setNumOfSessions(numOfSessions+1);

        results.setGlobal_id(1L);

        globalAverageDriveResultsRepository.save(results);
    }

    private void setAverageDriveResultsObject(AverageDriveResults results, AverageDriveResults averageDriveResults)
    {
        if(results.getUserId()==null)
        {
            results.setUserId(averageDriveResults.getUserId());
        }

        results.setMaxSpeed(Math.max(results.getMaxSpeed(), averageDriveResults.getMaxSpeed()));
        results.setMaxAcceleration(Math.max(results.getMaxAcceleration(), averageDriveResults.getMaxAcceleration()));
        results.setMinAcceleration(Math.min(results.getMinAcceleration(), averageDriveResults.getMinAcceleration()));

        int avgSpeed = (results.getAvgSpeed()*results.getTotalDuration()+
                averageDriveResults.getAvgSpeed()*averageDriveResults.getTotalDuration())/(results.getTotalDuration()+averageDriveResults.getTotalDuration());
        results.setAvgSpeed(avgSpeed);

        int avgDistance = (results.getAvgDistance()*results.getTotalDuration()+
                averageDriveResults.getAvgDistance()*averageDriveResults.getTotalDuration())
                /(results.getTotalDuration()+averageDriveResults.getTotalDuration());

        results.setAvgDistance(avgDistance);

        int avgDuration = ((results.getAvgDuration()*results.getNumOfSessions())+averageDriveResults.getTotalDuration())/(results.getNumOfSessions()+1);
        results.setAvgDuration(avgDuration);

        results.setTotalDuration(averageDriveResults.getTotalDuration()+ results.getTotalDuration());

        results.setTotalDistance(results.getTotalDistance() + averageDriveResults.getTotalDistance());

        results.setIdleTime(results.getIdleTime()+averageDriveResults.getIdleTime());
        int idlePercent = ((results.getIdleTime()/results.getTotalDuration())*100);
        results.setAvgIdleTime(idlePercent);

        results.setSuddenBreaks(results.getSuddenBreaks()+averageDriveResults.getSuddenBreaks());
        results.setSuddenAcceleration(results.getSuddenAcceleration()+averageDriveResults.getSuddenAcceleration());
        results.setSuddenTurns(results.getSuddenTurns()+averageDriveResults.getSuddenTurns());
        if(results.getTotalDistance()==0)
        {
            results.setAvgSuddenBreaks(0.0);
            results.setAvgSuddenAcceleration(0.0);
            results.setAvgSuddenTurns(0.0);
        }
        else
        {
            DecimalFormat df = new DecimalFormat("#.##");
            double breaksResult = (double) (results.getSuddenBreaks()*1000) / (double) results.getTotalDistance();
            double accelerationsResult = (double) (results.getSuddenAcceleration()*1000) / (double) results.getTotalDistance();
            double turnsResult = (double) (results.getSuddenTurns()*1000) / (double) results.getTotalDistance();


            results.setAvgSuddenBreaks(Double.parseDouble(df.format(breaksResult)));
            results.setAvgSuddenAcceleration(Double.parseDouble(df.format(accelerationsResult)));
            results.setAvgSuddenTurns(Double.parseDouble(df.format(turnsResult)));
        }

        int numOfSessions = results.getNumOfSessions();
        results.setNumOfSessions(numOfSessions+1);
    }
}
