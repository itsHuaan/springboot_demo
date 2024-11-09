package org.example.springboot_demo.controllers;

import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.services.impl.AttendanceService;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public HomeController(final AttendanceService attendanceService,
                          final EmployeeService employeeService,
                          final AttendanceMapper attendanceMapper,
                          final EmployeeMapper employeeMapper) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.attendanceMapper = attendanceMapper;
        this.employeeMapper = employeeMapper;
    }

    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("currentPath", "/dashboard");
        return "index";
    }

    @GetMapping("attendances")
    public String attendancesPage(Model model, @RequestParam(required = false) LocalDate date) {
        LocalDate dateAttendance = date != null ? date : LocalDate.now();
        List<AttendanceDto> attendances = attendanceService.getByDate(dateAttendance);
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

    @GetMapping("statistics")
    public String statisticsPage(Model model) {
        List<EmployeeDto> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("currentPath", "/statistics");
        return "statistics";
    }
}
