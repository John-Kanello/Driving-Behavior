package com.ergasia.DrivingBehavior.user.Repository;

import com.ergasia.DrivingBehavior.user.HelperClasses.LatAndLong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DangerousLocationsRepository extends JpaRepository<LatAndLong, Long> {
}
