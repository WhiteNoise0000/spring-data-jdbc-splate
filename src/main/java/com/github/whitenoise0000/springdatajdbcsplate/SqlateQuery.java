package com.github.whitenoise0000.springdatajdbcsplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
public class SqlateQuery implements RepositoryQuery {
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
		// TODO SQLパラメータ指定
		SqlTemplate template = query.get();
		ProcessResult processResult = template.process(getContext(parameters));
		Object[] params = processResult.getParameters().toArray();
		String sql = processResult.getSql();
		Class<?> retType = queryMethod.getReturnedObjectType();

		// DML(INSERT/UPDATE/DELETE)の場合
		if (sql.startsWith("INSERT") || sql.startsWith("UPDATE") || sql.startsWith("DELETE")) {
			return jdbcOperations.update(sql, params);
		}

		// SELECTの場合
		// →戻り型に応じた処理結果を返却
		if (List.class.equals(retType)) {
			return jdbcOperations.query(sql, rowMapper, params);
		}
		else if (Stream.class.equals(retType)) {
			return jdbcOperations.queryForStream(sql, rowMapper, params);
		}
		else if (Map.class.equals(retType)) {
			return jdbcOperations.queryForMap(sql, params);
		}
		// その他戻り値型は、1件取得の前提で扱う
		return jdbcOperations.queryForObject(processResult.getSql(), rowMapper, params);
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