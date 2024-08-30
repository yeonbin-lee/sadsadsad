package com.example.common_module.domain.member.repository;

import com.example.common_module.domain.member.entity.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
