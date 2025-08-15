package datos;

import java.sql.*;

public class Conexion {

    private static final String BD = "gmas2";
    private static final String URL = "jdbc:mysql://localhost:3306/" + BD;
    private static final String USER = "root";
    private static final String PASSWORD = "root";


    public static Connection getConexion() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa");
        } catch (SQLException e) {
            System.out.println("Error al intentar conectarse a la base de datos: " + e);
        }
        return connection;
    }


    public static void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexion cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }

    public static void cerrarStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
                System.out.println("Statement cerrado correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar el Statement: " + e.getMessage());
            }
        }
    }

    public static void cerrarResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
                System.out.println("ResultSet cerrado correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar el ResultSet: " + e.getMessage());
            }
        }
    }


}
