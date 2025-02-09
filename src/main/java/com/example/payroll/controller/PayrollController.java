package com.example.payroll.controller;

import com.example.payroll.model.Employee;
import com.example.payroll.model.Payslip;
import com.example.payroll.model.CompanyPolicy;
import com.example.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;



import java.util.List;

@CrossOrigin(origins = "*") // ✅ Allow requests from frontend

@RestController
@RequestMapping("/payroll")
//s@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return payrollService.getAllEmployees();
    }
    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        return payrollService.createEmployee(employee);
    }



    @PostMapping("/payslip/{id}")
    public Payslip generatePayslip(@PathVariable Long id, 
                                   @RequestParam double bonus,
                                   @RequestParam(required = false, defaultValue = "0") double totalHoursWorked) {
        return payrollService.generatePaySlip(id, bonus, totalHoursWorked);
    }

    @GetMapping("/payslip/{id}")
    public List<Payslip> getPayslips(@PathVariable Long id) {
        return payrollService.getPayslipsByEmployee(id);
    }

    @GetMapping("/company-policy")
    public CompanyPolicy getCompanyPolicy() {
        return payrollService.getCompanyPolicy();
    }
    @PostMapping("/company-policy")
    public CompanyPolicy createCompanyPolicy(@RequestBody CompanyPolicy policy) {
        return payrollService.createCompanyPolicy(policy);
    }

}
