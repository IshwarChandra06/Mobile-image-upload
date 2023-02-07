package com.eikona.tech.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Image;

@Repository
public interface ImageRepository  extends DataTablesRepository<Image, Long> {

	Image findByOriginalPath(String string);

	List<Image> findByEmployee(Employee emp);

}
