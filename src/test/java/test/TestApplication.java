package test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import io.github.whitenoise0000.springdatajdbcsplate.SplateRepositoryFactoryBean;

@SpringBootApplication
@EnableJdbcRepositories(repositoryFactoryBeanClass = SplateRepositoryFactoryBean.class)
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

}
