package com.example.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.payroll.model.CompanyPolicy;

public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy, Long> {
}
