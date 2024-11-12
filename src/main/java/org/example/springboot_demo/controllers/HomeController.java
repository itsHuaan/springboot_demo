package org.example.springboot_demo.controllers;

import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.dtos.OTRegistrationDto;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.mappers.impl.OTRegistrationMapper;
import org.example.springboot_demo.services.impl.AttendanceService;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.example.springboot_demo.services.impl.OTRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/")
public class HomeController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final OTRegistrationService otRegistrationService;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;
    private final OTRegistrationMapper otRegistrationMapper;

    @Autowired
    public HomeController(AttendanceService attendanceService, 
                          EmployeeService employeeService,
                          OTRegistrationService otRegistrationService,
                          AttendanceMapper attendanceMapper,
                          EmployeeMapper employeeMapper,
                          OTRegistrationMapper otRegistrationMapper) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.otRegistrationService = otRegistrationService;
        this.attendanceMapper = attendanceMapper;
        this.employeeMapper = employeeMapper;
        this.otRegistrationMapper = otRegistrationMapper;
    }

    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("currentPath", "/dashboard");
        return "index";
    }

    @GetMapping("attendances")
    public String attendancesPage(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        List<AttendanceDto> attendances =  attendanceService.getByDate(LocalDate.now());
        model.addAttribute("attendances", attendances);
        model.addAttribute("currentPath", "/attendances");
        return "attendances";
    }

    @GetMapping("employees")
    public String employeesPage(Model model) {
        List<EmployeeDto> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("currentPath", "/employees");
        return "employees";
    }

    @GetMapping("detailed")
    public String statisticsPage(Model model,
                                 @RequestParam(required = false) Long employeeId,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(required = false) Integer year) {
        List<EmployeeDto> employees = employeeService.findAll();
        List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");
        List<Integer> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 50; i--) {
            years.add(i);
        }
        List<AttendanceStatisticsDto> detailedStatistics = new ArrayList<>();
        if (employeeId != null && month != null && year != null) {
            detailedStatistics = attendanceService.getStatistics(employeeId, month, year);
        }
        model.addAttribute("detailedStatistics", detailedStatistics);
        model.addAttribute("months", months);
        model.addAttribute("years", years);
        model.addAttribute("employees", employees);
        model.addAttribute("currentPath", "/detailed");
        model.addAttribute("selectedEmployee", employeeId);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        return "statistics/detailed";
    }

    @GetMapping("overview")
    public String overviewPage(Model model) {
        List<EmployeeDto> employeeDtos = employeeService.findAll();
        List<String> emails = employeeDtos.stream().map(EmployeeDto::getEmail).filter(Objects::nonNull).toList();
        List<AttendanceStatisticsDto> overview = attendanceService.getStatistics(null, LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        model.addAttribute("overview", overview);
        model.addAttribute("emails", emails);
        model.addAttribute("currentPath", "/overview");
        return "statistics/overview";
    }

    @GetMapping("ot_registrations")
    public String otRegistrationsPage(Model model) {
        List<OTRegistrationDto> otRegistrations = otRegistrationService.findAll();
        model.addAttribute("otRegistrations", otRegistrations);
        model.addAttribute("currentPath", "/ot_registrations");
        return "ot/ot_registrations";
    }

    @GetMapping("ot_register")
    public String otRegisterPage(Model model) {
        List<EmployeeDto> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("currentPath", "/ot_register");
        return "ot/register_for_ot";
    }
}
