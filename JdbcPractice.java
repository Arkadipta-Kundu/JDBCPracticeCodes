package JDBCPracticeCodes;

// Import necessary SQL classes
import java.sql.*;
import java.util.Scanner;

public class JdbcPractice {
        public static void main(String[] args) {
                Connection conn = null;
                try {
                        // Establish a connection to the PostgreSQL database
                        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Demo", "postgres", "0000");
                        System.out.println("connected");
                        Statement st = conn.createStatement();

                        // Create a new table
                        String createTableQuery = "CREATE TABLE IF NOT EXISTS products (id SERIAL PRIMARY KEY, name VARCHAR(50), price DECIMAL)";
                        st.executeUpdate(createTableQuery);
                        System.out.println("Table 'products' created");

                        // Insert a new product
                        String insertProductQuery = "INSERT INTO products (name, price) VALUES ('Laptop', 999.99)";
                        st.executeUpdate(insertProductQuery);
                        System.out.println("Product inserted");

                        // Fetch a User by Age (READ operation)
                        Scanner sc = new Scanner(System.in);
                        System.out.print("enter the age: ");
                        int age = sc.nextInt();
                        System.out.println();
                        String query = "select name from users where age = " + age;
                        ResultSet rs = st.executeQuery(query);
                        while (rs.next()) {
                                System.out.println("User name is :" + rs.getString("name"));
                        }
                        System.out.println("executed");

                        // Update User Details (UPDATE operation)
                        query = "UPDATE users SET email = 'john.doe@updated.com' WHERE id = 1";
                        int res = st.executeUpdate(query);
                        System.out.println("email updated !");

                        // Delete a User (DELETE operation)
                        query = "DELETE from users where id = 1";
                        res = st.executeUpdate(query);
                        System.out.println("deleted !");

                        // Insert Users Using PreparedStatement
                        String queryPreparedStatement = "INSERT INTO users (name, email, age) VALUES (?, ?, ?);";
                        PreparedStatement pst = conn.prepareStatement(queryPreparedStatement);
                        pst.setString(1, "Hannah Baker");
                        pst.setString(2, "hannah@exaple.com");
                        pst.setInt(3, 23);
                        int result = pst.executeUpdate();
                        System.out.println("executed and " + result + " rows affected");

                        // Fetch a User by Email Using PreparedStatement
                        query = "select email from users where age = ?";
                        pst = conn.prepareStatement(query);
                        age = 22;
                        pst.setInt(1, age); 
                        rs = pst.executeQuery();
                        while (rs.next()) {
                                System.out.println("Email is for age " + age + " is " + rs.getString("email"));
                        }

                        // Update Age Using PreparedStatement
                        query = "UPDATE users SET age = ? WHERE id = ?";
                        pst = conn.prepareStatement(query);
                        pst.setInt(1, 29);
                        pst.setInt(2, 5);
                        result = pst.executeUpdate();
                        System.out.println(result + " rows affected");

                        // Perform Batch Insert Using PreparedStatement
                        query = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
                        pst = conn.prepareStatement(query);
                        pst.setString(1, "John Doe");
                        pst.setString(2, "john.doe@example.com");
                        pst.setInt(3, 28);
                        pst.addBatch();
                        pst.setString(1, "Jane Smith");
                        pst.setString(2, "jane.smith@example.com");
                        pst.setInt(3, 34);
                        pst.addBatch();
                        pst.setString(1, "Alice Johnson");
                        pst.setString(2, "alice.johnson@example.com");
                        pst.setInt(3, 26);
                        pst.addBatch();
                        int[] batchResult = pst.executeBatch();
                        System.out.println("batch insertion success");

                        // Perform Transaction (Insert Two Users, Then Rollback One)
                        conn.setAutoCommit(false);
                        String dataInsertQuery = "INSERT INTO accounts (name, account_id, balance) VALUES (?, ?, ?)";
                        PreparedStatement dataInsertStatement = conn.prepareStatement(dataInsertQuery);
                        dataInsertStatement.setString(1, "Bela Bose");
                        dataInsertStatement.setInt(2, 1110021319);
                        dataInsertStatement.setInt(3, 4900);
                        dataInsertStatement.executeUpdate();
                        dataInsertStatement.setString(1, "Amar Dube");
                        dataInsertStatement.setInt(2, 1159092359);
                        dataInsertStatement.setInt(3, 1530);
                        dataInsertStatement.executeUpdate();
                        conn.rollback(); // Rollback the second insert
                        conn.commit();
                        System.out.println("Data Inserted Successfully");

                        // Withdraw Money From Bank Account (Using Transactions & Rollback)
                        conn.setAutoCommit(false);
                        String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                        PreparedStatement withdrawStatement = conn.prepareStatement(withdrawQuery);
                        withdrawStatement.setInt(1, 2000);
                        withdrawStatement.setInt(2, 1150022312);
                        withdrawStatement.executeUpdate();
                        conn.rollback(); // Rollback the withdrawal
                        System.out.println("withdrawn and rollbacked");

                        // Transfer Money Between Two Accounts (Using Transactions)
                        conn.setAutoCommit(false);
                        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
                        PreparedStatement debitStatement = conn.prepareStatement(debitQuery);
                        PreparedStatement creditStatement = conn.prepareStatement(creditQuery);
                        debitStatement.setInt(1, 1200);
                        debitStatement.setInt(2, 1150022359);
                        int debitRes = debitStatement.executeUpdate();
                        creditStatement.setInt(1, 1200);
                        creditStatement.setInt(2, 1150022319);
                        int creditRes = creditStatement.executeUpdate();
                        if (debitRes > 0 && creditRes > 0) {
                                System.out.println("Transaction Successful");
                                conn.commit();
                        } else {
                                System.out.println("Transaction Failed");
                                conn.rollback();
                        }

                        // View users table
                        System.out.println();
                        String queryForView = "select * from users";
                        ResultSet urs = st.executeQuery(queryForView);
                        System.out.println("Your Database is :");
                        while (urs.next()) {
                                System.out.println("User name is :" + urs.getString("name") + " and email is: " + urs.getString("email") + " and age is " + urs.getString("age"));
                        }

                        // View accounts table
                        queryForView = "select * from accounts";
                        urs = st.executeQuery(queryForView);
                        System.out.println("Your Database is :");
                        while (urs.next()) {
                                System.out.println("AccountHolder name is : " + urs.getString("name") + " and account number is: " + urs.getString("account_id") + " and current balance is " + urs.getString("balance"));
                        }
                        System.out.println();
                        System.out.println();

                } catch (SQLException e) {
                        e.printStackTrace();
                } finally {
                        // Close the connection
                        if (conn != null) {
                                try {
                                        conn.close();
                                        System.out.println("connection closed");
                                } catch (SQLException e) {
                                        e.printStackTrace();
                                }
                        }
                }
        }
}
