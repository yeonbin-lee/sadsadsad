package com.example.common_module.domain.member.repository;

import com.example.common_module.domain.member.entity.Logout;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutRepository extends CrudRepository<Logout, String> {
}
