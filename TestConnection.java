import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://db.hgcesbylhkjoxtymxysy.supabase.co:5432/postgres";
        String user = "postgres";
        String password = "Placita2010$";

        System.out.println("Testing connection to: " + url);

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Connection successful!");
            System.out.println("Database version: " + conn.getMetaData().getDatabaseProductVersion());
            conn.close();
        } catch (SQLException e) {
            System.out.println("✗ Connection failed:");
            System.out.println("Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Vendor Error Code: " + e.getErrorCode());
        }
    }
}
