package ru.rsatu.dbkursach.db;

import javafx.util.Callback;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер БД, осуществляет работу с Базой Данных
 */
public class DbManager {
    private final Connection conn;
    private Callback<DbStatus, DbStatus> lastOperationStatusCallback;

    private static DbManager dbManager;

    public DbManager(String url, String user, String password) {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DbManager getInstance() {
        if (dbManager != null) {
            return dbManager;
        }
        String url = System.getProperty("jdbc.url");
        String user = System.getProperty("jdbc.user");
        String password = System.getProperty("jdbc.password");
        System.out.printf("Connecting to %s\n", url);
        dbManager = new DbManager(url, user, password);
        System.out.println("Connection open");
        return dbManager;
    }

    public void registerLastOperationStatusCallback(Callback<DbStatus, DbStatus> lastOperationStatusCallback) {
        this.lastOperationStatusCallback = lastOperationStatusCallback;
    }

    public String getDbUrl() {
        try {
            return conn.getMetaData().getURL();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    public <T> List<T> executeSqlReturnList(String sql, Class<T> type) {
        ResultSet rs = executeSql(sql);
        List<T> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(rsToObject(rs, type));
            }
            return list;
        } catch (SQLException e) {
            if (lastOperationStatusCallback != null)
                lastOperationStatusCallback.call(DbStatus.FAILED);
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeSql(String SQL) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (lastOperationStatusCallback != null)
                lastOperationStatusCallback.call(DbStatus.Ok);
            return rs;
        } catch (SQLException e) {
            if (lastOperationStatusCallback != null)
                lastOperationStatusCallback.call(DbStatus.FAILED);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public <T> T rsToObject(ResultSet rs, Class<T> type) {
        T obj = type.getConstructor().newInstance();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(obj, rs.getObject(field.getName(), field.getType()));
            field.setAccessible(false);
        }
        return obj;
    }

    @SneakyThrows
    public void closeConnection() {
        conn.close();
        System.out.println("Connection closed");
    }

    public void executeSqlNoReturn(String sql) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            if (lastOperationStatusCallback != null)
                lastOperationStatusCallback.call(DbStatus.Ok);
        } catch (SQLException e) {
            if (lastOperationStatusCallback != null)
                lastOperationStatusCallback.call(DbStatus.FAILED);
            throw new RuntimeException(e);
        }
    }
}
