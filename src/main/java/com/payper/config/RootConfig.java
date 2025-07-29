package com.payper.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({
        "classpath:/application.properties",
        "classpath:/application-db.properties",
        "classpath:/application-kakao.properties",
        "classpath:/application-codef.properties"
})
@ComponentScan(basePackages = {
        "com.payper.config",
        "com.payper.partner.service",
        "com.payper.codef.service",
        "com.payper.user.service"
})
@MapperScan(value = "com.payper", annotationClass = Mapper.class)
@Log4j2
@EnableTransactionManagement
public class RootConfig {
  @Value("${jdbc.driver}")
  private String driver;
  @Value("${jdbc.url}")
  private String url;
  @Value("${jdbc.username}")
  private String username;
  @Value("${jdbc.password}")
  private String password;

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driver);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);

    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
    sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
    sqlSessionFactory.setDataSource(dataSource());

    return sqlSessionFactory.getObject();
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
    return manager;
  }
}
