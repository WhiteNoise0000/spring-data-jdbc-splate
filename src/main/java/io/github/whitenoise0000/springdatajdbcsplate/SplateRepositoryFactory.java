package io.github.whitenoise0000.springdatajdbcsplate;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.lang.Nullable;

import com.github.mygreen.splate.SqlTemplateEngine;

class SplateRepositoryFactory extends JdbcRepositoryFactory {

	private final RelationalMappingContext context;
	private final JdbcConverter converter;
	private final NamedParameterJdbcOperations operations;
	private final SqlTemplateEngine splateEngine;

	public SplateRepositoryFactory(DataAccessStrategy dataAccessStrategy, RelationalMappingContext context,
			JdbcConverter converter, Dialect dialect, ApplicationEventPublisher publisher,
			NamedParameterJdbcOperations operations) {
		super(dataAccessStrategy, context, converter, dialect, publisher, operations);
		this.context = context;
		this.converter = converter;
		this.operations = operations;
		this.splateEngine = new SqlTemplateEngine();
	}

	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key,
			ValueExpressionDelegate valueExpressionDelegate) {
		Optional<QueryLookupStrategy> original = super.getQueryLookupStrategy(key, valueExpressionDelegate);
		return Optional.of(new SplateQueryLookupStrategy(operations.getJdbcOperations(), context, converter,
				original.orElseThrow(), splateEngine));
	}
}
