package presto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrestoAPI {
    public static void main(String[] args) throws Exception {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        Connection connection = DriverManager.getConnection(
                "jdbc:presto://master:8088/hive/default",
                "hadoop",
                "hadoop");

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("show tables");

        while(resultSet.next()){
            System.out.println(resultSet.getString(1));
        }
    }
}
