//package com.example.common_module.member.repository;
//
//import com.example.common_module.member.domain.entity.Auth;
//import com.example.common_module.member.domain.entity.Member;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface AuthRepository extends JpaRepository<Auth, Long> {
//
//    Boolean existsByMember(Member member);
//
//    Optional<Auth> findByRefreshToken(String refreshToken);
//}
