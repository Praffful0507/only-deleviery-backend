package com.prafful.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prafful.springjwt.models.UserMultipleAdress;

@Repository
public interface UserMultipleAdressRepository extends JpaRepository<UserMultipleAdress, Long>{

	List<UserMultipleAdress> findAllByEmail(String userName);

}
