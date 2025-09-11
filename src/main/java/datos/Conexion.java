package datos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Conexion {

    // Pool único para toda la app (se crea al primer uso)
    private static volatile HikariDataSource DS;

    private static HikariDataSource buildDs() {
        // Lee de variables de entorno con defaults (cámbialos si quieres)
        String host = getenv("DB_HOST", "viaduct.proxy.rlwy.net");
        String port = getenv("DB_PORT", "55075");
        String db   = getenv("DB_NAME", "gmas2");
        String user = getenv("DB_USER", "root");
        String pass = getenv("DB_PASS", "VjzJWzhoqWkVCKVwIMXnVpoZkjfodggT"); // <-- ideal: ponerlo en env

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true"
                + "&sslMode=REQUIRED"               // fuerza SSL (evita el error de la clave pública)
                + "&cachePrepStmts=true&prepStmtCacheSize=256&prepStmtCacheSqlLimit=2048"
                + "&useServerPrepStmts=true&rewriteBatchedStatements=true"
                + "&tcpKeepAlive=true&connectTimeout=2000&socketTimeout=10000";

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Tamaños/políticas del pool (ajusta a tu plan de Railway)
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(2_000); // ms a esperar una conexión del pool
        cfg.setIdleTimeout(120_000);     // ms
        cfg.setMaxLifetime(600_000);     // ms

        return new HikariDataSource(cfg);
    }

    private static String getenv(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isEmpty()) ? def : v;
    }

    private static HikariDataSource ds() {
        if (DS == null) {
            synchronized (Conexion.class) {
                if (DS == null) {
                    DS = buildDs();
                }
            }
        }
        return DS;
    }

    public static Connection getConexion() throws SQLException {
        return ds().getConnection(); // cerrar en try-with-resources -> regresa al pool
    }

    public static DataSource getDataSource() {
        return ds();
    }

    // Llamar al apagar la app (p.ej. en un ServletContextListener)
    public static void shutdown() {
        HikariDataSource d = DS;
        if (d != null) d.close();
    }
}
