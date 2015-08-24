package com.rs.lottoweb.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import com.rs.lottoweb.service.LottoService;

@MapperScan("com.rs.lottoweb.mapper")
@Configuration
@ComponentScan(basePackageClasses={LottoService.class})
public class AppConfig {

	
	@Bean
	public DataSource dataSource(){
		return new EmbeddedDatabaseBuilder()
				.setName("lottodb")
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("schema.sql")
				.addScript("data.sql")
				.build();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		return sessionFactoryBean.getObject();
	}

	
	@Bean
	public PlatformTransactionManager transactionManager(){
		return new DataSourceTransactionManager(dataSource());
	}
}
