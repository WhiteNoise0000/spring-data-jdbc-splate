package io.github.whitenoise0000.springdatajdbcsplate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.splate.SqlTemplateEngine;

/**
 * Splate(2Way-SQL)クエリ.
 *
 * @author WhiteNoise0000
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Splate {

	/**
	 * 2-WaySQLファイルパス.
	 * <p>
	 * Splateに引き渡す2-WaySQLのパスを指定する.
	 * </p>
	 * <ul>
	 * <li>何もつけない場合 - クラスパスから取得します。ex){@literal /sql/hoge.sql}</li>
	 * <li>{@literal classpath:} - クラスパスから取得します。ex){@literal classpath:/sql/hoge.sql}</li>
	 * <li>{@literal file:} - システムファイルから取得します。ex){@literal file:c:/sql/hoge.sql}</li>
	 * </ul>
	 *
	 * @return SQLファイルパス.
	 * @see SqlTemplateEngine#getTemplate(String)
	 */
	String value();
}
