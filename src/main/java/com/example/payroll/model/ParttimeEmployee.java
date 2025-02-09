package com.example.payroll.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("PARTTIME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParttimeEmployee extends Employee {

    private int hoursWorked;
    private double hourlyRate;

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}
