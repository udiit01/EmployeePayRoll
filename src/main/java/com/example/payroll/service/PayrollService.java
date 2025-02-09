
package com.example.payroll.service;

import com.example.payroll.model.*;
import com.example.payroll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private CompanyPolicyRepository companyPolicyRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    public Payslip generatePaySlip(Long employeeId, double bonus, double totalHoursWorked) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
    
        if (employee.isPresent()) {
            Employee emp = employee.get();
            CompanyPolicy policy = getCompanyPolicy();
    
            double standardHours = (policy != null) ? policy.getStandardWorkingHours() : 160;
            double overtimeMultiplier = (policy != null) ? policy.getOvertimeMultiplier() : 1.5;
            
            // ✅ Calculate Overtime Hours
            double overtimeHours = Math.max(0, totalHoursWorked - standardHours);
    
            double basicSalary = emp.calculateSalary();
            double overtimePay = 0;
    
            if (emp instanceof FulltimeEmployee) {
                overtimePay = ((FulltimeEmployee) emp).calculateSalary(overtimeMultiplier, standardHours, overtimeHours) - basicSalary;
            }
    
            double taxDeduction = basicSalary * 0.1;
            double insuranceDeduction = basicSalary * 0.05;
            double providentFund = basicSalary * 0.08;
            double totalDeductions = taxDeduction + insuranceDeduction + providentFund;
            double netSalary = basicSalary + bonus + overtimePay - totalDeductions;
    
            Payslip payslip = new Payslip(null, emp, basicSalary, bonus, overtimePay, 
                taxDeduction, insuranceDeduction, providentFund, totalDeductions, netSalary, LocalDate.now());
    
            return payslipRepository.save(payslip);
        }
        return null;
    }
    
    
    

    public List<Payslip> getPayslipsByEmployee(Long employeeId) {
        return payslipRepository.findByEmployeeId(employeeId);
    }
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    
    public CompanyPolicy getCompanyPolicy() {
        return companyPolicyRepository.findById(1L).orElse(new CompanyPolicy(1L, 1.5, 160));
    }
    public CompanyPolicy createCompanyPolicy(CompanyPolicy policy) {
        return companyPolicyRepository.save(policy);
    }
    
}
