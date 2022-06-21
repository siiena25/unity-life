package com.unitylife.demo;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {

  private static final String jdbcURL = "jdbc:postgresql://localhost:5433/coursework_db";
  private static final String jdbcUsername = "user";
  private static final String jdbcPassword = "password";

  public static void main(String[] args) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(jdbcURL, jdbcUsername, jdbcPassword);
    flyway.setValidateOnMigrate(false);
    flyway.repair();
    //flyway.clean();
    //flyway.migrate();
    SpringApplication.run(Main.class, args);
  }
}