package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class contains methods used to interact with database ticket table.
 *
 * @author Ludovic Tuccio
 */
public class TicketDAO {
   /**
    * TicketDAO logger.
    */
   private static final Logger LOGGER = LogManager.getLogger("TicketDAO");
   /**
    * DataBaseConfig object creation.
    */
   private DataBaseConfig dataBaseConfig = new DataBaseConfig();
   /**
    * Error message to display in some error loggers.
    */
   private static final String ERROR_MESSAGE =

               "Error fetching next available slot";
   /**
    * A static final int to avoid Magic Number.
    */
   private static final int SIX = 6;

   /**
    * DataBaseConfig setter.
    *
    * @param dbConfig
    */
   public void setDataBaseConfig(final DataBaseConfig dbConfig) {
      this.dataBaseConfig = dbConfig;
   }

   /**
    * Used to save tickets to database.
    *
    * @param ticket the Ticket to save
    * @return true if ticket was saved successfully or false if saving process
    *         failed
    */
   public boolean saveTicket(final Ticket ticket) {
      Connection con = null;
      PreparedStatement ps = null;
      int i = 1;
      try {
         con = dataBaseConfig.getConnection();
         ps = con.prepareStatement(DBConstants.SAVE_TICKET);
         ps.setInt(i, ticket.getParkingSpot().getId());
         i++;
         ps.setString(i, ticket.getVehicleRegNumber());
         i++;
         ps.setDouble(i, ticket.getPrice());
         i++;
         ps.setTimestamp(i, Timestamp.valueOf(ticket.getInTime()));
         i++;
         ps.setTimestamp(i, (ticket.getOutTime() == null) ? null
                     : (Timestamp.valueOf(ticket.getOutTime())));
         return ps.execute();
      } catch (Exception ex) {
         LOGGER.error(ERROR_MESSAGE, ex);
      } finally {
         dataBaseConfig.closeConnection(con);
         dataBaseConfig.closePreparedStatement(ps);
      }
      return false;
   }

   /**
    * Used to recover a database ticket.
    *
    * @param vehicleRegNumber a user's vehicle registration number
    * @return the latest ticket found in database
    */
   public Ticket getTicket(final String vehicleRegNumber) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      Ticket ticket = null;
      ParkingSpot spot;
      int i = 1;
      try {
         con = dataBaseConfig.getConnection();
         ps = con.prepareStatement(DBConstants.GET_TICKET);
         ps.setString(1, vehicleRegNumber);
         rs = ps.executeQuery();
         if (rs.next()) {
            ticket = new Ticket();
            spot = new ParkingSpot(rs.getInt(i),
                        ParkingType.valueOf(rs.getString(SIX)), false);
            i++;
            ticket.setParkingSpot(spot);
            ticket.setId(rs.getInt(i));
            i++;
            ticket.setVehicleRegNumber(vehicleRegNumber);
            ticket.setPrice(rs.getDouble(i));
            i++;
            ticket.setInTime(rs.getTimestamp(i).toLocalDateTime());
            i++;
            ticket.setOutTime((rs.getTimestamp(i) == null) ? null
                        : rs.getTimestamp(i).toLocalDateTime());
         }
      } catch (Exception ex) {
         LOGGER.error(ERROR_MESSAGE, ex);
      } finally {
         dataBaseConfig.closeConnection(con);
         dataBaseConfig.closeResultSet(rs);
         dataBaseConfig.closePreparedStatement(ps);
      }
      return ticket;
   }

   /**
    * Update the latest ticket for a vehicleRegNumber, updating price and
    * out-time.
    *
    * @param ticket the Ticket to update
    * @return boolean true if ticket success update, else false
    */
   public boolean updateTicket(final Ticket ticket) {
      Connection con = null;
      PreparedStatement ps = null;
      int i = 1;
      try {
         con = dataBaseConfig.getConnection();
         ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
         ps.setDouble(i, ticket.getPrice());
         i++;
         ps.setTimestamp(i, Timestamp.valueOf(ticket.getOutTime()));
         i++;
         ps.setInt(i, ticket.getId());
         ps.execute();
      } catch (Exception ex) {
         LOGGER.error("Error saving ticket info", ex);
         return false;
      } finally {
         dataBaseConfig.closeConnection(con);
         dataBaseConfig.closePreparedStatement(ps);
      }
      return true;
   }

   /**
    * Check the number of paid tickets for a registration to determine visits
    * number.
    *
    * @param vehicleRegNumber the user's vehicle registration number
    * @return numberVisitsUser the total vehicle visits number
    */
   public int checkNumberVisitsUser(final String vehicleRegNumber) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      int numberVisitsUser = 0;
      try {
         con = dataBaseConfig.getConnection();
         ps = con.prepareStatement(DBConstants.CHECK_EXISTING_OLD_TICKETS);
         ps.setString(1, vehicleRegNumber);
         rs = ps.executeQuery();
         if (rs.next()) {
            numberVisitsUser = rs.getInt(1);
         }
      } catch (ClassNotFoundException | SQLException ex) {
         LOGGER.error("Error during check existing old tickets process.", ex);
      } finally {
         dataBaseConfig.closeConnection(con);
         dataBaseConfig.closeResultSet(rs);
         dataBaseConfig.closePreparedStatement(ps);
      }
      return numberVisitsUser;
   }
}
