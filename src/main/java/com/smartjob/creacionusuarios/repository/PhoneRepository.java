package com.smartjob.creacionusuarios.repository;

import com.smartjob.creacionusuarios.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
