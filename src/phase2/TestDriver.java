package phase2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class TestDriver {

    public static void displayMenu()
    {
        System.out.println("        Welcome to UUber System     ");
        System.out.println("1. register a user:");
        System.out.println("2. enter your own query:");
        System.out.println("3. exit:");
        System.out.println("pleasse enter your choice:");
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("Test Driver");
        Connector2 con=null;
        String choice;
        String un;
        String pw;
        String uname;
        String addr;
        String phone;
        String sql=null;
        int c=0;

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

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while(true)
            {
                displayMenu();
                while ((choice = in.readLine()) == null && choice.length() == 0);
                try{
                    c = Integer.parseInt(choice);
                }catch (Exception e)
                {

                    continue;
                }
                if (c<1 | c>3)
                    continue;
                if (c==1)
                {
                    System.out.println("please enter a username:");
                    while ((un = in.readLine()) == null && un.length() == 0);
                    System.out.println("please enter a password:");
                    while ((pw = in.readLine()) == null && pw.length() == 0);
                    System.out.println("please enter a name:");
                    while ((uname = in.readLine()) == null && uname.length() == 0);
                    System.out.println("please enter an address:");
                    while ((addr = in.readLine()) == null && addr.length() == 0);
                    System.out.println("please enter a phone #:");
                    while ((phone = in.readLine()) == null && phone.length() == 0);

                    DbUserService service = new DbUserService();
                    service.createUberUser(con.stmt, un, pw, uname, addr, phone);
                }
                else if (c==2)
                {
                    System.out.println("please enter your query below:");
                    while ((sql = in.readLine()) == null && sql.length() == 0)
                        System.out.println(sql);
                    ResultSet rs=con.stmt.executeQuery(sql);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numCols = rsmd.getColumnCount();
                    while (rs.next())
                    {
                        //System.out.print("cname:");
                        for (int i=1; i<=numCols;i++)
                            System.out.print(rs.getString(i)+"  ");
                        System.out.println("");
                    }
                    System.out.println(" ");
                    rs.close();
                }
                else
                {
                    System.out.println("EoM");
                    con.stmt.close();

                    break;
                }
            }
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

                catch (Exception e) { /* ignore close errors */ }
            }
        }
    }
}
