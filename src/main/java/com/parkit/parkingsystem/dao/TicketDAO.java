package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    private LocalDateTime inTime = LocalDateTime.now();
    private LocalDateTime outTime = LocalDateTime.now();
    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket) {
	Connection con = null;
	try {
	    con = dataBaseConfig.getConnection();
	    PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);

	    // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	    // ps.setInt(1,ticket.getId());
	    ps.setInt(1, ticket.getParkingSpot().getId());
	    ps.setString(2, ticket.getVehicleRegNumber());
	    ps.setDouble(3, ticket.getPrice());

	    inTime = ticket.getInTime();
	    ps.setObject(4, inTime.getMinute());

	    outTime = ticket.getOutTime();
	    ps.setObject(5, outTime.getMinute());

	    // ps.setObject(5, (ticket.getOutTime() == null) ? null : (new
	    // Timestamp(ticket.getOutTime().toMillis())));

	    // ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new
	    // Timestamp(ticket.getOutTime().toMillis())));

	    return ps.execute();

	} catch (Exception ex) {
	    logger.error("Error fetching next available slot", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    return false;
	}
    }

    public Ticket getTicket(String vehicleRegNumber) {
	Connection con = null;
	Ticket ticket = null;
	try {
	    con = dataBaseConfig.getConnection();
	    PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
	    // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	    ps.setString(1, vehicleRegNumber);
	    ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(rs.getInt(2));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(rs.getDouble(3));

		inTime = ticket.getInTime();
		java.sql.Timestamp.valueOf(inTime).getTime();

		outTime = ticket.getInTime();
		java.sql.Timestamp.valueOf(outTime);

//		ticket.setInTime(rs.getTimestamp(4));
//		ticket.setOutTime(rs.getTimestamp(5));
	    }
	    dataBaseConfig.closeResultSet(rs);
	    dataBaseConfig.closePreparedStatement(ps);
	} catch (Exception ex) {
	    logger.error("Error fetching next available slot", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    return ticket;
	}
    }

    public boolean updateTicket(Ticket ticket) {
	Connection con = null;
	try {
	    con = dataBaseConfig.getConnection();
	    PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
	    ps.setDouble(1, ticket.getPrice());

	    // ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
	    outTime = ticket.getOutTime();
	    ps.setObject(2, outTime.getMinute());

	    ps.setInt(3, ticket.getId());
	    ps.execute();
	    return true;
	} catch (Exception ex) {
	    logger.error("Error saving ticket info", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	}
	return false;
    }
}
