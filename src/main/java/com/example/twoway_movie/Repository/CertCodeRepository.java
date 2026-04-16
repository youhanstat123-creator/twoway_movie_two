package com.example.twoway_movie.Repository;

import com.example.twoway_movie.Entity.CertCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertCodeRepository extends JpaRepository<CertCode, String> {
    Optional<CertCode> findTopBySessionIdOrderByIssuedAtDesc(Long sessionId);
}
