package com.autoservice.auto_service.repository;

import com.autoservice.auto_service.model.Appointment;
import com.autoservice.auto_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserOrderByDateDescTimeDesc(User user);
    long countByUser(User user);
}
