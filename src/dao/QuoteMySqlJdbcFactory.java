package dao;

import service.QuoteDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class QuoteMySqlJdbcFactory extends DAOFactory {

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATA_BASE_NAME = "quotes";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "322223";

    // Setup the connection with the DB
    protected static Connection getConnection() {
        // TODO Добавить пул соединений
        try {
            return DriverManager.getConnection(JDBC_URL + DATA_BASE_NAME, LOGIN, PASSWORD);
        } catch(SQLException e) {
            //throw new DBSystemException("Can't create connection", e);
            return null;
        }
    }


        //String dbName = "quotes";

        //String SELECT_ALL_SQL = "SELECT * FROM  quote";
        //String INSERT_SQL = "INSERT INTO qoute ()";
        //String RENAME_COLUMN_SQL = "Alter table qoute CHANGE quoteText text text";
        //String RENAME_TABLE_SQL = "ALTER TABLE qoute RENAME AS Quote";




            //int n  = statement.executeUpdate(RENAME_TABLE_SQL);
            //ResultSet resultSet = statement.executeQuery(SELECT_ALL_SQL);

            //int n  = statement.executeUpdate(RENAME_COLUMN_SQL);


    /** This will load the MySQL driver, each DB has its own driver */
    protected static void initDriver() {
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't initialize driver: " + DRIVER_CLASS_NAME);
        }
    }


    @Override
    public QuoteDAO createQuoteDAO() {
        return new QuoteJdbcMySqlDAO();
    }
}
