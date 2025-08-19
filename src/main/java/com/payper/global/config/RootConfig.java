package com.payper.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@PropertySources({
        @PropertySource("classpath:/application.properties"),
        @PropertySource(value = "classpath:/application-db.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:/application-kakao.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:/application-codef.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:/application-secret.properties", ignoreResourceNotFound = true)
})
@ComponentScan(
        basePackages = "com.payper",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = RestController.class)
        }
)
@MapperScan(value = "com.payper", annotationClass = Mapper.class)
@Slf4j
@EnableTransactionManagement
@EnableCaching
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


/*    config.setMaximumPoolSize(100);          // 최대 100개 커넥션 (1000명 / 10 = 100)
    config.setMinimumIdle(50);               // 최소 50개 유지 (피크타임 대비)

    config.setConnectionTimeout(3000);       // 3초 커넥션 대기 (빠른 실패)
    config.setIdleTimeout(300000);           // 5분 유휴 타임아웃
    config.setMaxLifetime(600000);           // 10분 최대 생존시간
    config.setLeakDetectionThreshold(15000); // 15초 누수 탐지
    config.setValidationTimeout(2000);       // 2초 검증 타임아웃*/

//    jdbc레벨 캐싱 최적화
//    config.addDataSourceProperty("cachePrepStmts", "true");
//    config.addDataSourceProperty("prepStmtCacheSize", "100");        // 1000 → 100
//    config.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");   // 2048 → 1024
//    config.addDataSourceProperty("useServerPrepStmts", "false");     // true → false (중요!)
//    config.addDataSourceProperty("rewriteBatchedStatements", "true"); // 유지
    return new HikariDataSource(config);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource());
    factoryBean.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
    return factoryBean.getObject();
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager("myCards", "cardSearch");
  }
}
