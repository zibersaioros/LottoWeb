package com.rs.lottoweb.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@MapperScan("com.rs.lottoweb.mapper")
@Configuration
public class AppConfig {
	
	

	//자동설정이라 필요 없음.
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

	
	//자동 설정이라 필요 없음.
//	@Bean
//	public PlatformTransactionManager transactionManager(DataSource dataSource){
//		return new DataSourceTransactionManager(dataSource);
//	}
}
