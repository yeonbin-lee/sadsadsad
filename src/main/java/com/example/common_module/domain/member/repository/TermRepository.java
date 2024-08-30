package com.example.common_module.domain.member.repository;

import com.example.common_module.domain.member.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findByMandatoryTrue();
}
