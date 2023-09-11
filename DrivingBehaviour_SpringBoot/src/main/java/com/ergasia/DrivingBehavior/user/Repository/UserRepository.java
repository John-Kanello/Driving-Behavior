package com.ergasia.DrivingBehavior.user.Repository;

import com.ergasia.DrivingBehavior.user.Classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends  JpaRepository<User, Long>{


}