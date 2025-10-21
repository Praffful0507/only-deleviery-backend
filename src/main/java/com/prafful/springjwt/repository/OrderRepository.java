package com.prafful.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prafful.springjwt.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findAllByUserId(String emailId);

}
