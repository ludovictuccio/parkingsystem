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

/**
 * This class establishes connection to database and closes them.
 *
 * @author Ludovic Tuccio
 */
public class DataBaseConfig {

   /**
    * DataBaseConfig logger.
    */
   private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");
   /**
    * Informations properties file for DB connection.
    */
   private static final String CREDENTIALS_SECURITY =

               "src/main/resources/SQL_credentials_security.properties";
   /**
    * URL used to connect application to DB.
    */
   private String url;
   /**
    * User name that will be used for connecting to MySQL database.
    */
   private String userName;
   /**
    * Password that will be used for connecting to MySQL database.
    */
   private String password;

   /**
    * Create a MySQL database connection.
    *
    * @return a Connection instance
    * @throws ClassNotFoundException
    * @throws SQLException
    */
   public Connection getConnection()
               throws ClassNotFoundException, SQLException {
      LOGGER.debug("Create DB connection");
      Class.forName("com.mysql.cj.jdbc.Driver");
      try (InputStream is = new FileInputStream(CREDENTIALS_SECURITY)) {
         Properties properties = new Properties();
         properties.load(is);
         url = properties.getProperty("url");
         userName = properties.getProperty("userName");
         password = properties.getProperty("password");
      } catch (FileNotFoundException fnf) {
         LOGGER.error("File not found. "
                     + "Please verify credentials_file access root.", fnf);
      } catch (IOException ioex) {
         LOGGER.error("Error during DB connection."
                     + " Please check the contents file.", ioex);
      }
      return DriverManager.getConnection(url, userName, password);
   }

   /**
    * Close established Connection.
    *
    * @param con the Connection to close
    */
   public void closeConnection(final Connection con) {
      if (con != null) {
         try {
            con.close();
            LOGGER.debug("Closing DB connection");
         } catch (SQLException e) {
            LOGGER.error("Error while closing connection", e);
         }
      }
   }

   /**
    * Close established PreparedStatement.
    *
    * @param ps the PreparedStatement to close
    */
   public void closePreparedStatement(final PreparedStatement ps) {
      if (ps != null) {
         try {
            ps.close();
            LOGGER.debug("Closing Prepared Statement");
         } catch (SQLException e) {
            LOGGER.error("Error while closing prepared statement", e);
         }
      }
   }

   /**
    * Close established ResultSet.
    *
    * @param rs the ResultSet to close
    */
   public void closeResultSet(final ResultSet rs) {
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
