package com.sparta.post03.repository;

import com.sparta.post03.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRopository extends JpaRepository<RefreshToken, Long> {

}
