package com.example.payroll.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "employeeType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FulltimeEmployee.class, name = "FULLTIME"),
    @JsonSubTypes.Type(value = ParttimeEmployee.class, name = "PARTTIME")
})
public abstract class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    public abstract double calculateSalary();
}
