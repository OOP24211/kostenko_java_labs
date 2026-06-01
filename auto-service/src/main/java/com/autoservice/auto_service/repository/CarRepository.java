package com.autoservice.auto_service.repository;

import com.autoservice.auto_service.model.Car;
import com.autoservice.auto_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    // автомобили пользователя
    List<Car> findByUser(User user);

    // количество автомобилей у пользователя
    long countByUser(User user);
}