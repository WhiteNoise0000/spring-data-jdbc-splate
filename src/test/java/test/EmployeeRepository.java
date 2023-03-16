package test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import io.github.whitenoise0000.springdatajdbcsplate.Splate;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

	@Splate("/sql/sampleQuery.sql")
	List<Employee> queryForList(Integer salaryMin, Integer salaryMax);

	@Splate("/sql/sampleQuery.sql")
	Stream<Employee> queryForStream(Integer salaryMin, Integer salaryMax);

	@Splate("/sql/sampleQuery2.sql")
	Optional<Employee> querySingle(Long id);

	@Splate("/sql/sampleQuery2.sql")
	Employee querySingle2(Long id);

	@Splate("/sql/sampleInsert.sql")
	int sampleInsert(Long id, String name, Integer salary);

	@Splate("/sql/sampleCount.sql")
	long sampleCount(Integer salaryMin, Integer salaryMax);
}
