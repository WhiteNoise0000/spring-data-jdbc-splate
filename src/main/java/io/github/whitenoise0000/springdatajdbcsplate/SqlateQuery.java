package io.github.whitenoise0000.springdatajdbcsplate;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.util.Lazy;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.splate.EmptyValueSqlTemplateContext;
import com.github.mygreen.splate.MapSqlTemplateContext;
import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.splate.SqlTemplateEngine;

/**
 * Splate(2-waySQL)クエリ実行.
 * 
 * @author WhiteNoise0000
 */
class SqlateQuery implements RepositoryQuery {
	private Splate annotation;

	private JdbcOperations jdbcOperations;

	private RowMapper<?> rowMapper;

	private QueryMethod queryMethod;

	private SqlTemplateEngine splateEngine;

	Lazy<SqlTemplate> query = Lazy.of(this::getQuery);

	public SqlateQuery(Splate annotation, JdbcOperations jdbcOperations, RowMapper<?> rowMapper,
			QueryMethod queryMethod, SqlTemplateEngine sqlTemplateEngine) {
		this.annotation = annotation;
		this.jdbcOperations = jdbcOperations;
		this.rowMapper = rowMapper;
		this.queryMethod = queryMethod;
		this.splateEngine = sqlTemplateEngine;
	}

	@Override
	public Object execute(Object[] parameters) {

		// SQLパラメータ指定
		SqlTemplate template = query.get();
		ProcessResult processResult = template.process(getContext(parameters));
		Object[] params = processResult.getParameters().toArray();
		String sql = processResult.getSql();

		// DML(INSERT/UPDATE/DELETE)の場合
		if (sql.startsWith("INSERT") || sql.startsWith("UPDATE") || sql.startsWith("DELETE")) {
			return jdbcOperations.update(sql, params);
		}

		// SELECTの場合
		// →戻り型に応じた処理結果を返却
		if (queryMethod.isCollectionQuery() || queryMethod.isStreamQuery() || queryMethod.isPageQuery()
				|| queryMethod.isSliceQuery()) {
			// 複数件返却
			return jdbcOperations.query(sql, rowMapper, params);
		} else {
			// 1件返却
			Class<?> retType = queryMethod.getReturnedObjectType();
			if (queryMethod.isQueryForEntity()) {
				return jdbcOperations.queryForObject(sql, rowMapper, params);
			}
			// プリミティブ型、および独自DTOなど(1件返却)
			return jdbcOperations.queryForObject(sql, retType, params);
		}
	}

	@Override
	public QueryMethod getQueryMethod() {
		return queryMethod;
	}

	private SqlTemplate getQuery() {
		String location = annotation.value();
		SqlTemplate template = splateEngine.getTemplate(location);
		return template;
	}

	private SqlTemplateContext<StandardEvaluationContext> getContext(Object[] params) {

		// 引数なし
		if (queryMethod.getParameters().isEmpty()) {
			return new EmptyValueSqlTemplateContext();
		}

		// TODO Bean引数
		// 引数あり
		MapSqlTemplateContext context = new MapSqlTemplateContext();
		queryMethod.getParameters().forEach(param -> {
			context.addVariable(param.getName().get(), params[param.getIndex()]);
		});
		return context;
	}
}