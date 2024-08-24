package com.example.common_module.member.repository;

import com.example.common_module.member.domain.entity.FindEmail;
import org.springframework.data.repository.CrudRepository;

public interface FindEmailRepository extends CrudRepository<FindEmail, String> {
}
