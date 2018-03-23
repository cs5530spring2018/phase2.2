package phase2;/*import java.util.*;

public class InitializeDb {

    /**
     * @param args

    public static void main(String[] args) {

        String hostname;
        String username;
        String password;
        String dbName;

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter server hostname: ");
        hostname = sc.next();
        System.out.println("Enter username: ");
        username = sc.next();
        System.out.println("Enter password: ");
        password = sc.next();
        System.out.println("Enter db name: ");
        dbName = sc.next();

        try
        {
            //remember to replace the password
            con= new Connector2(hostname, username, password, dbName);
            System.out.println ("Database connection established");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println ("Either connection error or query execution error!");
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.closeConnection();
                    System.out.println ("Database connection terminated");
                }

                catch (Exception e) { /* ignore close errors }
            }
        }
    }
}
*/