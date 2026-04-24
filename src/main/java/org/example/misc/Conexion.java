package org.example.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexion a la base de datos MySQL.
 *
 * <p>
 * Usa el driver JDBC de MySQL (com.mysql.cj.jdbc.Driver) para establecer
 * la conexion con la base de datos local.
 * </p>
 *
 * <p><b>Datos de conexion:</b></p>
 * <ul>
 *   <li>Host: 127.0.0.1 (maquina local)</li>
 *   <li>Puerto: 3306</li>
 *   <li>Base de datos: biblioteca</li>
 *   <li>Usuario: root</li>
 * </ul>
 *
 * <p><b>Nota de seguridad:</b> En produccion, las credenciales deben moverse
 * a variables de entorno o archivos de configuracion externos.</p>
 *
 * <p><b>Patron aplicado:</b> Clase utilitaria estatica (no instanciable).</p>
 *
 * @author Josue
 * @version 1.0
 */
public class Conexion {

    /** Direccion IP del servidor MySQL. */
    private static final String HOST = "127.0.0.1";

    /** Puerto del servidor MySQL. */
    private static final String PUERTO = "3306";

    /** Nombre de la base de datos. */
    private static final String BD = "biblioteca";

    /** Usuario de la base de datos. */
    private static final String USER = "root";

    /** Contrasena del usuario de la base de datos. */
    private static final String PASS = "191419";

    /**
     * URL de conexion JDBC construida con los parametros anteriores.
     * Se deshabilita SSL y se permite recuperacion de clave publica.
     */
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BD +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * Constructor privado para evitar instancias de esta clase utilitaria.
     */
    private Conexion() {
    }

    /**
     * Obtiene una nueva conexion a la base de datos MySQL.
     *
     * <p>
     * Carga el driver de MySQL antes de intentar la conexion.
     * Si el driver no esta en el classpath, lanza una SQLException.
     * </p>
     *
     * @return Objeto {@link Connection} listo para usar.
     * @throws SQLException Si el driver no se encuentra o la conexion falla.
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Cargar el driver de MySQL en el classpath
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver MySQL en el classpath.", e);
        }
        // Retornar la conexion usando las credenciales configuradas
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Alias del metodo obtenerConexion() para compatibilidad con otros repositorios.
     *
     * @return Objeto {@link Connection} listo para usar.
     * @throws SQLException Si ocurre un error al conectar.
     */
    public static Connection getConnection() throws SQLException {
        return obtenerConexion();
    }
}
