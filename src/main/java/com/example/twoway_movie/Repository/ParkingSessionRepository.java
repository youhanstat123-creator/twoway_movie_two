package com.example.twoway_movie.Repository;

import com.example.twoway_movie.Entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
}
