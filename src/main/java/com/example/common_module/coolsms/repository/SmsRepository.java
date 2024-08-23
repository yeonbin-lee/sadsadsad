package com.example.common_module.coolsms.repository;

import com.example.common_module.coolsms.model.entity.Sms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRepository extends CrudRepository<Sms, String> {

    @Override
    boolean existsById(String phone); // key 값은 phone으로 한다
}
