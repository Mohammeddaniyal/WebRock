package booby.test;
import java.sql.*;
public class DAOConnection {
    private DAOConnection()
    {}
    public static Connection getConnection()
    {
        Connection connection=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hrdb", "hr","hr");
        } catch (Exception e) {
            System.out.println("SQL Exception : "+e);
            // TODO: handle exception
        }
        return connection;
    }
}
