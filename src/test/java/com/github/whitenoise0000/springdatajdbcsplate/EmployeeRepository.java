package com.github.whitenoise0000.springdatajdbcsplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	
	@Splate("/sql/sampleQuery.sql")
	List<Employee> queryForList(Integer salaryMin, Integer salaryMax);
	
	@Splate("/sql/sampleQuery.sql")
	Stream<Employee> queryForStream(Integer salaryMin, Integer salaryMax);

	// FIXME Employee querySingle()とするとエラーが起きる（要調査）
	@Splate("/sql/sampleQuery2.sql")
	Optional<Employee> querySingle(Long id);
	
	@Splate("/sql/sampleInsert.sql")
	int sampleInsert(Long id, String name, Integer salary);
}
