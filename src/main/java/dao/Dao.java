package dao;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * すべてのDaoの親クラス(抽象クラス)。DB接続の取得だけを担当する。
 * 接続先は META-INF/context.xml の Resource(name="jdbc/Movie")で決まる。
 */
public abstract class Dao {

	/** JNDI名。context.xml の Resource の name と、大文字・小文字も含めて合わせること */
	private static final String JNDI_NAME = "java:/comp/env/jdbc/Movie";

	/** DB接続の元(最初に使うときに1回だけ取得して使い回す) */
	private static volatile DataSource ds;

	/** DB接続を取得する。使い終わったら close() すること(try-with-resources 推奨) */
	protected Connection getConnection() throws Exception {
		if (ds == null) {
			synchronized (Dao.class) {
				if (ds == null) {
					InitialContext ic = new InitialContext();
					ds = (DataSource) ic.lookup(JNDI_NAME);
				}
			}
		}
		return ds.getConnection();
	}
}
