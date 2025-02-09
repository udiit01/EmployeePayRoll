package com.example.payroll.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("FULLTIME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FulltimeEmployee extends Employee {

    private double monthlySalary;

    // ✅ Fix: Change standardHours from int → double
    public double calculateSalary(double overtimeMultiplier, double standardHours, double overtimeHours) {
        double baseHourlyRate = monthlySalary / standardHours;
        double overtimePay = overtimeHours * (baseHourlyRate * overtimeMultiplier);
        return monthlySalary + overtimePay;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}
