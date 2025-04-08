package org.ylabHomework.serviceClasses.SpringConfigs;


import liquibase.integration.spring.SpringLiquibase;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
/**
 * Класс для конфигурации базы данных. Настраивает Liquibase, создает таблицу для хранения сервисных данных,
 * производит миграции, создает объект DataSource для работы с БД на основе application.yaml.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Configuration
@ComponentScan(basePackages = "org.ylabHomework")
@PropertySource(value = "classpath:application.yaml", factory = DBConfig.YamlPropertySourceFactory.class)
public class DBConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        String url = env.getProperty("db.url");
        String username = env.getProperty("db.username");
        String password = env.getProperty("db.password");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, Environment env) {
        String url = env.getProperty("db.url");
        assert url != null;
        String username = env.getProperty("db.username");
        String password = env.getProperty("db.password");

        try (Connection con = DriverManager.getConnection(url, username, password)) {
            con.setAutoCommit(false);
            try (Statement statement = con.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS service_schema");

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException("Ошибка при создании таблиц: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД: " + e.getMessage(), e);
        }
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog(env.getProperty("db.changeLogFile"));
            liquibase.setDefaultSchema("service_schema");
            liquibase.setShouldRun(true);
            return liquibase;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public static class YamlPropertySourceFactory implements PropertySourceFactory {
        @Override
        @NonNull
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name, @NonNull EncodedResource resource) throws IOException {
            try (InputStream inputStream = resource.getInputStream()) {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                Map<String, Object> map = yaml.load(inputStream);
                Properties properties = new Properties();
                flattenMap(map, properties, "");
                return new PropertiesPropertySource(name != null ? name : "applicationYaml", properties);
            }
        }

        @SuppressWarnings("unchecked")
        private void flattenMap(Map<String, Object> source, Properties target, String prefix) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Map) {
                    flattenMap((Map<String, Object>) value, target, key);
                } else {
                    target.put(key, value.toString());
                }
            }
        }
    }
}