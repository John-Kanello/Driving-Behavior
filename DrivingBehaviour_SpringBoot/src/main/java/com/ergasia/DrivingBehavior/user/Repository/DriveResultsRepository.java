package com.ergasia.DrivingBehavior.user.Repository;

import com.ergasia.DrivingBehavior.user.Classes.DriveResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveResultsRepository extends JpaRepository<DriveResults, Long> {

}
