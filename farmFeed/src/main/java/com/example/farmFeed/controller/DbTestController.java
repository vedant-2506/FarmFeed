package com.example.farmFeed.controller;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class DbTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-test")
    public String testDbConnection() {
        try (Connection con = dataSource.getConnection()) {
            return "JDBC Connected Successfully to: " + con.getMetaData().getURL();
        } catch (Exception e) {
            return "JDBC Connection Failed: " + e.getMessage();
        }
    }
}
