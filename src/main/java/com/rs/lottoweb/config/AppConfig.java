package com.rs.lottoweb.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.rs.lottoweb.mapper")
@EnableTransactionManagement
public class AppConfig {
	
	

	//자동설정이라 필요 없음. (Spring Boot는 pom.xml의 hsqldb를 보고 자동으로 datasource를 셋팅)
//	@Bean
//	public DataSource dataSource(){
//		return new EmbeddedDatabaseBuilder()
//				.setName("lottodb")
//				.setType(EmbeddedDatabaseType.HSQL)
//				.addScript("schema.sql")
//				.addScript("data.sql")
//				.build();
//	}
	
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		return sessionFactoryBean.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate( SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	
//	@Bean
//	public SqlSessionFactory fundSqlSessionFactory(@Qualifier("serviceDataSource") DataSource serviceDataSource,
//			ApplicationContext applicationContext) throws Exception {
//		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//		sqlSessionFactoryBean.setDataSource(serviceDataSource);
//		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:kr/co/beyondfund/fund/**/mapper/*.xml"));
//		SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
//		factory.getConfiguration().setMapUnderscoreToCamelCase(true);
//		factory.getConfiguration().getTypeAliasRegistry().registerAlias("Timestamp", Timestamp.class);
//		return factory;
//	}
	
}
