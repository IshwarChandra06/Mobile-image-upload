package com.eikona.tech.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.Role;


@Repository
public interface RoleRepository extends DataTablesRepository<Role, Long>{
	
	List<Role> findAllByIsDeletedFalse();

	Role findByName(String string);

	List<Role> findByOrganizationAndIsDeletedFalse(Organization organization);
}
