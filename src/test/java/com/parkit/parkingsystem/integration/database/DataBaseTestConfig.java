package com.parkit.parkingsystem.integration.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");
    private static final String SQL_CREDENTIALS_SECURITY = "src/main/resources/SQL_credentials_security.properties";
    private String url;
    private String userName;
    private String password;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
	LOGGER.debug("Create DB connection");
	Class.forName("com.mysql.cj.jdbc.Driver");
	try (InputStream inputStream = new FileInputStream(SQL_CREDENTIALS_SECURITY)) {
	    Properties properties = new Properties();
	    properties.load(inputStream);
	    url = properties.getProperty("url");
	    userName = properties.getProperty("userName");
	    password = properties.getProperty("password");
	} catch (FileNotFoundException fnf) {
	    LOGGER.error("File not found. Please verify sql_credentials_file access root.", fnf);
	} catch (IOException ioex) {
	    LOGGER.error("Error during SQL connection. Please check the contents of the file.", ioex);
	}
	return DriverManager.getConnection(url, userName, password);
    }

    public void closeConnection(Connection con) {
	if (con != null) {
	    try {
		con.close();
		LOGGER.debug("Closing DB connection");
	    } catch (SQLException e) {
		LOGGER.error("Error while closing connection", e);
	    }
	}
    }

    public void closePreparedStatement(PreparedStatement ps) {
	if (ps != null) {
	    try {
		ps.close();
		LOGGER.debug("Closing Prepared Statement");
	    } catch (SQLException e) {
		LOGGER.error("Error while closing prepared statement", e);
	    }
	}
    }

    public void closeResultSet(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close();
		LOGGER.debug("Closing Result Set");
	    } catch (SQLException e) {
		LOGGER.error("Error while closing result set", e);
	    }
	}
    }
}
