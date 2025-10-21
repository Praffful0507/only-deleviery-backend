package com.prafful.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prafful.springjwt.models.BillingInfo;

@Repository
public interface BillinfoRepository extends JpaRepository<BillingInfo, String>  {

}
