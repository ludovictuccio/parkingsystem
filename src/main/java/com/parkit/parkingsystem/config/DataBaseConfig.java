package com.parkit.parkingsystem.config;

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

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");
    private static final String sql_credentials_file = "src/main/resources/SQL_credentials_security.properties";
    private String url;
    private String userName;
    private String password;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
	logger.debug("Create DB connection");
	Class.forName("com.mysql.cj.jdbc.Driver");
	try (InputStream inputStream = new FileInputStream(sql_credentials_file)) {
	    Properties properties = new Properties();
	    properties.load(inputStream);
	    url = properties.getProperty("url");
	    userName = properties.getProperty("userName");
	    password = properties.getProperty("password");
	} catch (FileNotFoundException ex_fnf) {
	    logger.error("File not found. Please verify sql_credentials_file access root.", ex_fnf);
	} catch (IOException ex_io) {
	    logger.error("Error during SQL connection. Please check the contents of the file.", ex_io);
	}
	return DriverManager.getConnection(url, userName, password);
    }

    public void closeConnection(Connection con) {
	if (con != null) {
	    try {
		con.close();
		logger.debug("Closing DB connection");
	    } catch (SQLException e) {
		logger.error("Error while closing connection", e);
	    }
	}
    }

    public void closePreparedStatement(PreparedStatement ps) {
	if (ps != null) {
	    try {
		ps.close();
		logger.debug("Closing Prepared Statement");
	    } catch (SQLException e) {
		logger.error("Error while closing prepared statement", e);
	    }
	}
    }

    public void closeResultSet(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close();
		logger.debug("Closing Result Set");
	    } catch (SQLException e) {
		logger.error("Error while closing result set", e);
	    }
	}
    }
}
