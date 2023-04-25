package com.eikona.tech.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Organization;


@Repository
public interface OrganizationRepository extends DataTablesRepository<Organization, Long> {

	List<Organization> findAllByIsDeletedFalse();
	
	Organization  findByNameAndIsDeletedFalse(String string);

	List<Organization> findByIdAndIsDeletedFalse(Long id);

	List<Organization> findByCreatedByAndIsDeletedFalse(String createdBy);

}
