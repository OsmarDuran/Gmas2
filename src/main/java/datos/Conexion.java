package datos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Conexion {

    private static final HikariDataSource DS;

    static {
        // Lee de variables de entorno si existen, con fallback
        String host = getenv("DB_HOST", "viaduct.proxy.rlwy.net");
        String port = getenv("DB_PORT", "55075");
        String db   = getenv("DB_NAME", "railway");
        String user = getenv("DB_USER", "root");
        String pass = getenv("DB_PASS", "VjzJWzhoqWkVCKVwIMXnVpoZkjfodggT");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&useSSL=false&serverTimezone=UTC"
                + "&cachePrepStmts=true&prepStmtCacheSize=256&prepStmtCacheSqlLimit=2048"
                + "&useServerPrepStmts=true&rewriteBatchedStatements=true"
                + "&tcpKeepAlive=true&connectTimeout=2000&socketTimeout=10000";

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        // Ajusta a tu plan de Railway (pocas conexiones, pero calientes)
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setIdleTimeout(120_000);     // 2 min
        cfg.setConnectionTimeout(2_000); // 2 s para esperar una conex
        cfg.setMaxLifetime(600_000);     // 10 min
        cfg.setPoolName("Gmas2Pool");

        DS = new HikariDataSource(cfg);
    }

    private static String getenv(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isEmpty()) ? def : v;
    }

    public static Connection getConexion() throws SQLException {
        return DS.getConnection();
    }

    public static DataSource getDataSource() {
        return DS;
    }
}