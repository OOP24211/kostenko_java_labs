package com.autoservice.auto_service.controller;

import com.autoservice.auto_service.model.Appointment;
import com.autoservice.auto_service.model.Car;
import com.autoservice.auto_service.model.User;
import com.autoservice.auto_service.repository.AppointmentRepository;
import com.autoservice.auto_service.repository.CarRepository;
import com.autoservice.auto_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Получаем автомобили пользователя
        List<Car> cars = carRepository.findByUser(user);
        long carCount = carRepository.countByUser(user);
        long appointmentsCount = appointmentRepository.countByUser(user);
        var appointments = appointmentRepository.findByUserOrderByDateDescTimeDesc(user);
        model.addAttribute("user", user);
        model.addAttribute("cars", cars);
        model.addAttribute("carCount", carCount);

        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentsCount", appointmentsCount);

        return "profile";
    }

    @PostMapping("/profile/add-car")
    public String addCar(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String brand,
                         @RequestParam String model,
                         @RequestParam Integer year,
                         @RequestParam(required = false) String licensePlate) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Car car = new Car(brand, model, year, licensePlate, user);
        carRepository.save(car);

        return "redirect:/profile";
    }
    @PostMapping("/profile/delete-car/{id}")
    public String deleteCar(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Car car = carRepository.findById(id).orElse(null);

        // Проверяем, что автомобиль принадлежит текущему пользователю (безопасность!)
        if (car != null && car.getUser().getId().equals(user.getId())) {
            carRepository.delete(car);
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/delete-appointment/{id}")
    public String deleteAppointment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Appointment appointment = appointmentRepository.findById(id).orElse(null);

        if (appointment != null && appointment.getUser().getId().equals(user.getId())) {
            appointmentRepository.delete(appointment);
        }

        return "redirect:/profile";
    }

}