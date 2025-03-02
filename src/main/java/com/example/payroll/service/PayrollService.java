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

            // Calculate Overtime Hours
            double overtimeHours = Math.max(0, totalHoursWorked - standardHours);
            double yearlySalary = emp.calculateSalary();
            double basicMonthlySalary = (yearlySalary - 75000) / 12; // Standard deduction applied
            double overtimePay = 0;

            if (emp instanceof FulltimeEmployee) {
                overtimePay = ((FulltimeEmployee) emp).calculateSalary(overtimeMultiplier, standardHours, overtimeHours) - basicMonthlySalary;
            }

            double taxDeduction = calculateTaxDeduction(yearlySalary);
            double insuranceDeduction = basicMonthlySalary * 0.05; // 5% insurance deduction
            double providentFund = basicMonthlySalary * 0.08; // 8% provident fund deduction
            double totalDeductions = taxDeduction + insuranceDeduction + providentFund;
            double netMonthlySalary = basicMonthlySalary + bonus + overtimePay - totalDeductions;

            Payslip payslip = new Payslip(null, emp, basicMonthlySalary, bonus, overtimePay,
                    taxDeduction, insuranceDeduction, providentFund, totalDeductions, netMonthlySalary, LocalDate.now());

            return payslipRepository.save(payslip);
        }
        return null;
    }

    private double calculateTaxDeduction(double yearlySalary) {
        double tax = 0;
        double standardDeduction = 75000;
        double taxableIncome = yearlySalary - standardDeduction;

        if (yearlySalary <= 1200000) {
            return 0; // No tax if yearly salary is 12L or less
        }

        if (yearlySalary > 1200000 && yearlySalary <= 1270000) {
            double excessIncome = yearlySalary - 1200000;
            return excessIncome / 12; // Marginal relief
        }

        // Apply tax slabs
        if (taxableIncome <= 400000) {
            tax = taxableIncome * 0.05;
        } else if (taxableIncome <= 800000) {
            tax = (400000 * 0.05) + ((taxableIncome - 400000) * 0.10);
        } else if (taxableIncome <= 1200000) {
            tax = (400000 * 0.05) + (400000 * 0.10) + ((taxableIncome - 800000) * 0.15);
        } else if (taxableIncome <= 1600000) {
            tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + ((taxableIncome - 1200000) * 0.20);
        } else if (taxableIncome <= 2000000) {
            tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + (400000 * 0.20) + ((taxableIncome - 1600000) * 0.25);
        } else {
            tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + (400000 * 0.20) + (400000 * 0.25) + ((taxableIncome - 2000000) * 0.30);
        }

        return tax / 12; // Convert yearly tax to monthly tax deduction
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
