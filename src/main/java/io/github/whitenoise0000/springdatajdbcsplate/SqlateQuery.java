package io.github.whitenoise0000.springdatajdbcsplate;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.util.Lazy;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.splate.BeanPropertySqlTemplateContext;
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

		// 単一引数
		Iterator<? extends Parameter> it = queryMethod.getParameters().iterator();
		if (it.hasNext()) {
			Parameter param = it.next();
			if (!it.hasNext()) {
				Class<?> type = param.getType();
				// JavaBeanとして展開する型のみ、BeanPropertySqlTemplateContext を利用する
				if (isJavaBeanArgument(type)) {
					Object value = params[param.getIndex()];
					if (value == null) {
						throw new IllegalArgumentException(
								"@Splate method '" + queryMethod.getName()
										+ "' does not accept a null JavaBean argument."
										+ " Provide a non-null JavaBean instance.");
					}
					return new BeanPropertySqlTemplateContext(value);
				}
			}
		}

		// 引数あり(複数 / 単一スカラー)
		MapSqlTemplateContext context = new MapSqlTemplateContext();
		queryMethod.getParameters().forEach(param -> {
			context.addVariable(param.getName().get(), params[param.getIndex()]);
		});
		return context;
	}

	/**
	 * 単一引数をJavaBeanとして展開する対象かどうかを判定する.
	 *
	 * <p>{@code false} を返す型は、Bean展開対象外として従来通り引数名で
	 * {@link MapSqlTemplateContext} に登録される.</p>
	 */
	private static boolean isJavaBeanArgument(Class<?> type) {
		if (type == null) {
			return false;
		}
		if (type.isPrimitive()) {
			return false;
		}
		if (type.isArray()) {
			return false;
		}
		if (Map.class.isAssignableFrom(type)) {
			return false;
		}
		if (Iterable.class.isAssignableFrom(type)) {
			return false;
		}
		if (Optional.class.isAssignableFrom(type)) {
			return false;
		}
		if (Number.class.isAssignableFrom(type)) {
			return false;
		}
		if (Boolean.class.equals(type)) {
			return false;
		}
		if (Character.class.equals(type)) {
			return false;
		}
		if (Enum.class.isAssignableFrom(type)) {
			return false;
		}
		if (String.class.equals(type)) {
			return false;
		}
		if (Date.class.isAssignableFrom(type)) {
			return false;
		}
		if (UUID.class.equals(type)) {
			return false;
		}
		if (type.getPackageName().startsWith("java.time")) {
			return false;
		}
		return true;
	}
}
