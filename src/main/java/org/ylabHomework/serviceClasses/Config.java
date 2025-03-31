package org.ylabHomework.serviceClasses;


import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс для установки сессии соединения с базой данных и Liquibase.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 15.03.2025
 * </p>
 */
public class Config {

    private static final Properties property = new Properties();

    static {
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/config.properties")) {
            property.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка! " + e);
        }
    }

    public static void init() {
        String username = property.getProperty("username");
        String password = property.getProperty("password");
        String url = property.getProperty("url");

        try (Connection con = DriverManager.getConnection(url, username, password)) {
            con.setAutoCommit(false);
            try {
                con.setAutoCommit(false);

                con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS service_schema");
                con.createStatement().execute("SET search_path TO service_schema");


                JdbcConnection jdbcConnection = new JdbcConnection(con);
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
                Liquibase liquibase = new Liquibase(property.getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);
                liquibase.update();

                con.commit();
            } catch (SQLException | LiquibaseException e) {
                con.rollback();
                throw new RuntimeException("Ошибка миграций! " + e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка! " + e);
        }
    }


    /**
     * Подключается к базе данных в соответствии с данными, прописанными в config.properties.
     *
     * @return соединение с базой данных.
     */
    public Connection establishConnection() throws SQLException {


        String username = property.getProperty("username");
        String password = property.getProperty("password");
        String url = property.getProperty("url");

        Connection con = DriverManager.getConnection(url, username, password);
        con.setAutoCommit(false);
        return con;

    }
}