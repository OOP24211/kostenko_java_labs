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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/booking")
    public String showBookingForm(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(required = false) String service,
                                  @RequestParam(required = false) Integer price,
                                  Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Car> cars = carRepository.findByUser(user);

        model.addAttribute("cars", cars);
        model.addAttribute("selectedService", service);
        model.addAttribute("selectedPrice", price);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeSlots", getTimeSlots());

        return "booking";
    }

    @PostMapping("/booking")
    public String createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam Long carId,
                                @RequestParam String serviceName,
                                @RequestParam Integer price,
                                @RequestParam LocalDate date,
                                @RequestParam LocalTime time,
                                @RequestParam(required = false) String comment) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Car car = carRepository.findById(carId)
                               .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setCar(car);
        appointment.setServiceName(serviceName);
        appointment.setPrice(price);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setComment(comment);

        appointmentRepository.save(appointment);

        return "redirect:/profile?booked";
    }

    private List<String> getTimeSlots() {
        return List.of("09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
    }
}
