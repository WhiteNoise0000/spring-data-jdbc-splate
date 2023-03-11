package com.github.whitenoise0000.springdatajdbcsplate;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	
	@Splate("/sql/sampleQuery.sql")
	List<Employee> queryForList(Integer salaryMin, Integer salaryMax);
	
	@Splate("/sql/sampleQuery.sql")
	List<Employee> sampleQuery(Employee condition);
}
