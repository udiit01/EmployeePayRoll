package com.example.payroll.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private double basicSalary;
    private double bonus;
    private double overtimePay;
    private double taxDeduction;
    private double insuranceDeduction;
    private double providentFund;
    private double totalDeductions;
    private double netSalary;
    private LocalDate payslipDate;
}
