package com.github.whitenoise0000.springdatajdbcsplate;

import java.lang.reflect.Method;

import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.JdbcOperations;

import com.github.mygreen.splate.SqlTemplateEngine;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class SplateQueryLookupStrategy implements QueryLookupStrategy {

	private final JdbcOperations jdbcOperations;
	private final RelationalMappingContext context;
	private final JdbcConverter converter;
	private final QueryLookupStrategy defaultStrategy;
	private final SqlTemplateEngine splateEngine;

	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
			NamedQueries namedQueries) {
		
		QueryMethod queryMethod = new QueryMethod(method, metadata, factory);
		EntityRowMapper<?> rowMapper = new EntityRowMapper<>(
				context.getRequiredPersistentEntity(metadata.getDomainType()), converter);

		Splate annotation = method.getAnnotation(Splate.class);
		if (annotation != null) {
			return new SqlateQuery(annotation, jdbcOperations, rowMapper, queryMethod, splateEngine);
		}

		return defaultStrategy.resolveQuery(method, metadata, factory, namedQueries);
	}

}
