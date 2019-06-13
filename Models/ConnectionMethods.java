package Models;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionMethods {

    public static String user, pass;
    private static String connString = "jdbc:postgresql://localhost:5432/libraryforteachers?" +
            "useUnicode=true" +
            "&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false" +
            "&serverTimezone=UTC";

    private static Connection connection;
    private static Statement statement;

    /**
     * Creates an instance of a <code>ConnectionMethods</code> object in order to being able to
     * connect to a database while using its methods.
     * @param u a String that may contain the user for the database
     * @param p a String that may contain the password for that user in the database
     */
    private ConnectionMethods(String u, String p) {
        user = u;
        pass = p;
    }

    /**
     * Executes an arbitrary query
     * @param sql a String that represents an arbitrary SQL query
     * @return the <code>ResultSet</code> produced by executing the query
     * @throws SQLException if a database error occurs or this method is called on a closed connection
     */
    public static ResultSet executeQuery(String sql) throws SQLException {
        ResultSet resultSet;

        connection = DriverManager.getConnection(connString, user, pass);
        // Statement
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        // ResultSet
        resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    /**
     * Executes an arbitrary update
     * @param sql a String that may contain an arbitrary SQL update query
     * @throws SQLException if a database error occurs or if this method is called on a closed connection
     */
    public static void executeUpdate(String sql) throws SQLException {
        connection = DriverManager.getConnection(connString, user, pass);

        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        statement.executeUpdate(sql);

        connection.close();
        statement.close();
    }

    public static String citiesProcedure(@NotNull ArrayList<String> inParam) throws SQLException {
        String result;

        connection = DriverManager.getConnection(connString, user, pass);
        CallableStatement callable = connection.prepareCall("{ ? = call insertupdatecity(?, ?, ?, ?) }");
        callable.registerOutParameter(5, Types.VARCHAR);
        callable.setInt(1, Integer.parseInt(inParam.get(0)));
        callable.setString(2, inParam.get(1));
        callable.setString(3, inParam.get(2));
        callable.setInt(4, Integer.parseInt(inParam.get(3)));

        callable.execute();
        result = callable.getString(5);
        callable.close();

        return result;
    }

    /**
     * Prepares a call for any existing Stored Procedure in the current database.
     * @param sql an SQL statement that may contain one or more '?' parameter placeholders
     * @param inParamMap a HashMap that may contain one or more sets for variable declaring purposes
     * @param inParam an ArrayList that may contain one or more values for each parameter placeholder
     * @param outParam a HashMap that contains one or more sets for out parameter-declaring purposes
     * @return the value of the out parameter
     * @throws SQLException if a database error occurs
     */
    public static String prepareCall(String sql, HashMap<Integer, String> inParamMap, ArrayList<String> inParam,
                              @NotNull HashMap<Integer, String> outParam) throws SQLException {
        String result;
        int mapKey = 0;

        connection = DriverManager.getConnection(connString, user, pass);
        CallableStatement callable = connection.prepareCall(sql);

        for (Integer key : outParam.keySet()) {
            mapKey = key;
            switch (outParam.get(key).toLowerCase()) {
                case "int":
                    callable.registerOutParameter(key, Types.INTEGER);
                    break;
                case "string":
                    callable.registerOutParameter(key, Types.VARCHAR);
                    break;
                case "bit":
                    callable.registerOutParameter(key, Types.BOOLEAN);
                    break;
            }
        }

        for (Integer key : inParamMap.keySet()) {
            switch (inParamMap.get(key).toLowerCase()) {
                case "int":
                    callable.setInt(key, Integer.parseInt(inParam.get(key - 1)));
                    break;
                case "string":
                    callable.setString(key, inParam.get(key - 1));
                    break;
                case "double":
                    callable.setDouble(key, Double.parseDouble(inParam.get(key - 1)));
                    break;
                case "bit":
                    callable.setBoolean(key, Boolean.parseBoolean(inParam.get(key - 1)));
                    break;

            }
        }

        callable.execute();
        result = Boolean.toString(callable.getBoolean(mapKey));
        callable.close();

        return result;
    }

    public static void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        if (statement != null) {
            statement.close();
        }
    }

}