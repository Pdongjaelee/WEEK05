package com.sparta.post03.repository;


import com.sparta.post03.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>
{

    Optional<Member> findByUsername(String username);
}
