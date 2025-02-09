package com.example.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.payroll.model.Payslip;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeId(Long employeeId);
}
