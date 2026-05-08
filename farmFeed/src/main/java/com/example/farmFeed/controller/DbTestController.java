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

    @GetMapping("/db-info")
    public java.util.Map<String, Object> getDbInfo() {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        try (Connection con = dataSource.getConnection()) {
            java.sql.DatabaseMetaData metaData = con.getMetaData();
            java.sql.ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            java.util.List<java.util.Map<String, Object>> tables = new java.util.ArrayList<>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                java.util.Map<String, Object> tableInfo = new java.util.HashMap<>();
                tableInfo.put("name", tableName);
                
                // Count rows
                try (java.sql.Statement stmt = con.createStatement();
                     java.sql.ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`")) {
                    if (countRs.next()) {
                        tableInfo.put("rows", countRs.getInt(1));
                    }
                } catch (Exception e) {
                    tableInfo.put("error", e.getMessage());
                }
                tables.add(tableInfo);
            }
            info.put("status", "connected");
            info.put("url", metaData.getURL());
            info.put("tables", tables);
        } catch (Exception e) {
            info.put("status", "error");
            info.put("error", e.getMessage());
        }
        return info;
    }
}
