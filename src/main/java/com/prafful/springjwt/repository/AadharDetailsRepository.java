package com.prafful.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prafful.springjwt.models.AadharDetails;

@Repository
public interface AadharDetailsRepository extends JpaRepository<AadharDetails, String>{

}
