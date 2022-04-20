package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBHelper {
    private static String dbUrl = System.getProperty("dbUrl");
    private static String dbUser = System.getProperty("dbUser");
    private static String dbPass = System.getProperty("dbPass");

    @SneakyThrows
    public static void clearDB() {
        var cleanCreditRequest = "DELETE FROM credit_request_entity;";
        var cleanOrder = "DELETE FROM order_entity;";
        var cleanPayment = "DELETE FROM payment_entity;";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            runner.update(conn, cleanCreditRequest);
            runner.update(conn, cleanOrder);
            runner.update(conn, cleanPayment);
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
    }

    @SneakyThrows
    public static String getTransactionStatusDebitCard()  {
        var sqlQuery = "SELECT status FROM payment_entity WHERE id IS NOT NULL;";
        Thread.sleep(500);
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             var countStmt = conn.createStatement();
             var rs = countStmt.executeQuery(sqlQuery);) {
            if (rs.next()) {
                var status = rs.getString("status");
                return status;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }

    @SneakyThrows
    public static String getTransactionTypeDebitCard() {
        var sqlQuery = "SELECT payment_id FROM order_entity WHERE id IS NOT NULL;";
        Thread.sleep(500);
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             var countStmt = conn.createStatement();
             var rs = countStmt.executeQuery(sqlQuery);) {
            if (rs.next()) {
                var paymentId = rs.getString("payment_id");
                return paymentId;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }

    @SneakyThrows
    public static String getTransactionStatusCreditCard() {
        var sqlQuery = "SELECT status FROM credit_request_entity WHERE id IS NOT NULL;";
        Thread.sleep(500);
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             var countStmt = conn.createStatement();
             var rs = countStmt.executeQuery(sqlQuery);) {
            if (rs.next()) {
                var status = rs.getString("status");
                return status;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }

    @SneakyThrows
    public static String getTransactionTypeCreditCard() {
        var sqlQuery = "SELECT credit_id FROM order_entity WHERE id IS NOT NULL;";
        Thread.sleep(500);
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             var countStmt = conn.createStatement();
             var rs = countStmt.executeQuery(sqlQuery);) {
            if (rs.next()) {
                var paymentId = rs.getString("payment_id");
                return paymentId;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }
}
