/*
 * Copyright 2017-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.whitenoise0000.springdatajdbcsplate;

import java.io.Serializable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.DataAccessStrategyFactory;
import org.springframework.data.jdbc.core.convert.InsertStrategyFactory;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.core.convert.SqlParametersFactory;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

/**
 * Spring Data JDBC向けSplate(2Way-SQL)ラッパー用FactoryBean.
 * 
 * <p>このFactoryBeanは、Splateを使用したRepositoryの生成をサポートします。
 * Splateは、2Way-SQLを使用したデータアクセスを提供し、より直観的で効率的なデータ操作を可能にします。</p>
 * 
 * @see org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean
 */
public class SplateRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
    extends JdbcRepositoryFactoryBean<T, S, ID> {

    private ApplicationEventPublisher publisher;
    private BeanFactory beanFactory;
    private RelationalMappingContext mappingContext;
    private JdbcConverter converter;
    private DataAccessStrategy dataAccessStrategy;
    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
    private NamedParameterJdbcOperations operations;
    private EntityCallbacks entityCallbacks;
    private Dialect dialect;

    /**
     * 指定されたRepositoryインターフェース用の {@link SplateRepositoryFactoryBean} を新規作成します。
     * 
     * @param repositoryInterface Repositoryインターフェースのクラス、nullにできません。
     */
    public SplateRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * アプリケーションイベントパブリッシャーを設定します。
     *
     * @param publisher 設定するApplicationEventPublisher
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        super.setApplicationEventPublisher(publisher);
        this.publisher = publisher;
    }

    /**
     * 実際の {@link RepositoryFactorySupport} インスタンスを生成します。
     *
     * @return 生成されたRepositoryFactorySupportインスタンス
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        // SplateRepositoryFactoryを生成
        SplateRepositoryFactory jdbcRepositoryFactory = new SplateRepositoryFactory(dataAccessStrategy, mappingContext,
                converter, dialect, publisher, operations);
        jdbcRepositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
        jdbcRepositoryFactory.setEntityCallbacks(entityCallbacks);
        jdbcRepositoryFactory.setBeanFactory(beanFactory);

        return jdbcRepositoryFactory;
    }

    /**
     * リレーショナルマッピングコンテキストを設定します。
     *
     * @param mappingContext 設定するRelationalMappingContext
     */
    @Override
    public void setMappingContext(RelationalMappingContext mappingContext) {
        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    /**
     * ダイアレクトを設定します。
     *
     * @param dialect 設定するDialect
     */
    @Override
    public void setDialect(Dialect dialect) {
        super.setDialect(dialect);
        this.dialect = dialect;
    }

    /**
     * データアクセスストラテジーを設定します。
     *
     * @param dataAccessStrategy 設定するDataAccessStrategy
     */
    @Override
    public void setDataAccessStrategy(DataAccessStrategy dataAccessStrategy) {
        super.setDataAccessStrategy(dataAccessStrategy);
        this.dataAccessStrategy = dataAccessStrategy;
    }

    /**
     * クエリマッピング設定を設定します。
     *
     * @param queryMappingConfiguration 設定するQueryMappingConfiguration
     */
    @Autowired(required = false)
    @Override
    public void setQueryMappingConfiguration(QueryMappingConfiguration queryMappingConfiguration) {
        super.setQueryMappingConfiguration(queryMappingConfiguration);
        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    /**
     * JDBC操作を設定します。
     *
     * @param operations 設定するNamedParameterJdbcOperations
     */
    @Override
    public void setJdbcOperations(NamedParameterJdbcOperations operations) {
        super.setJdbcOperations(operations);
        this.operations = operations;
    }

    /**
     * コンバーターを設定します。
     *
     * @param converter 設定するJdbcConverter
     */
    @Override
    public void setConverter(JdbcConverter converter) {
        super.setConverter(converter);
        this.converter = converter;
    }

    /**
     * BeanFactoryを設定します。
     *
     * @param beanFactory 設定するBeanFactory
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    /**
     * プロパティが設定された後に呼び出されます。
     * 必要なプロパティが設定されていることを検証し、不足している場合はデフォルト値を設定します。
     */
    @Override
    public void afterPropertiesSet() {

        Assert.state(this.mappingContext != null, "MappingContext is required and must not be null");
        Assert.state(this.converter != null, "RelationalConverter is required and must not be null");

        if (this.operations == null) {

            Assert.state(beanFactory != null, "If no JdbcOperations are set a BeanFactory must be available");

            this.operations = beanFactory.getBean(NamedParameterJdbcOperations.class);
        }

        if (this.dataAccessStrategy == null) {

            Assert.state(beanFactory != null, "If no DataAccessStrategy is set a BeanFactory must be available");

            this.dataAccessStrategy = this.beanFactory.getBeanProvider(DataAccessStrategy.class) //
                    .getIfAvailable(() -> {

                        Assert.state(this.dialect != null, "Dialect is required and must not be null");

                        SqlGeneratorSource sqlGeneratorSource = new SqlGeneratorSource(this.mappingContext, this.converter,
                                this.dialect);
                        SqlParametersFactory sqlParametersFactory = new SqlParametersFactory(this.mappingContext, this.converter);
                        InsertStrategyFactory insertStrategyFactory = new InsertStrategyFactory(this.operations, this.dialect);

                        DataAccessStrategyFactory factory = new DataAccessStrategyFactory(sqlGeneratorSource, this.converter,
                                this.operations, sqlParametersFactory, insertStrategyFactory);

                        return factory.create();
                    });
        }

        if (this.queryMappingConfiguration == null) {
            this.queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
        }

        if (beanFactory != null) {
            entityCallbacks = EntityCallbacks.create(beanFactory);
        }

        super.afterPropertiesSet();
    }
}
