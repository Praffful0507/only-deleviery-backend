package com.prafful.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prafful.springjwt.models.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, String> {

}
