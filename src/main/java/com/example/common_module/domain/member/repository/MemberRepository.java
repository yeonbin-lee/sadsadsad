package com.example.common_module.domain.member.repository;

import com.example.common_module.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

//    @Query("select * from ")
//    boolean existByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByNickname(String nickname);
    Optional<Member> findByPhone(String phone);
}
