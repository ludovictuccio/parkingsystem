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

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    private static final String ERROR_MESSAGE = "Error fetching next available slot";
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
	this.dataBaseConfig = dataBaseConfig;
    }

    public boolean saveTicket(Ticket ticket) {
	Connection con = null;
	PreparedStatement ps = null;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.SAVE_TICKET);
	    ps.setInt(1, ticket.getParkingSpot().getId());
	    ps.setString(2, ticket.getVehicleRegNumber());
	    ps.setDouble(3, ticket.getPrice());
	    ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
	    ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (Timestamp.valueOf(ticket.getOutTime())));
	    return ps.execute();
	} catch (Exception ex) {
	    logger.error(ERROR_MESSAGE, ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	Ticket ticket = null;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.GET_TICKET);
	    ps.setString(1, vehicleRegNumber);
	    rs = ps.executeQuery();
	    if (rs.next()) {
		ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(rs.getInt(2));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(rs.getDouble(3));
		ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
		ticket.setOutTime((rs.getTimestamp(5) == null) ? null : rs.getTimestamp(5).toLocalDateTime());
	    }
	} catch (Exception ex) {
	    logger.error(ERROR_MESSAGE, ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closeResultSet(rs);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
	Connection con = null;
	PreparedStatement ps = null;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
	    ps.setDouble(1, ticket.getPrice());
	    ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
	    ps.setInt(3, ticket.getId());
	    ps.execute();
	} catch (Exception ex) {
	    logger.error("Error saving ticket info", ex);
	    return false;
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return true;
    }

    public int checkNumberVisitsUser(String vehicleRegNumber) {
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
	    logger.error(ERROR_MESSAGE, ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closeResultSet(rs);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return numberVisitsUser;
    }
}