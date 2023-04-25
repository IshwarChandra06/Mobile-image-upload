package com.eikona.tech.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.User;


@Repository
public interface UserRepository extends DataTablesRepository<User, Long>{
	
	List<User> findAllByIsDeletedFalse();
	
	User findByUserNameAndIsDeletedFalse(String name);
}
