package org.ylabHomework.serviceClasses.springConfigs.db;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    private final DataSource dataSource;

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Value("${spring.liquibase.default-schema}")
    private String defaultSchema;

    public LiquibaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @DependsOn("dataSourceInitializer")
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(defaultSchema);
        return liquibase;
    }
}