package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * This class contains methods used to interact with database parking table.
 */
public class ParkingSpotDAO {
    /**
     * ParkingSpotDAO logger.
     */
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");
    /**
     * DataBaseConfig object creation.
     */
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Setter of dataBaseConfig.
     * 
     * @param dataBaseConfig
     */
    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
	this.dataBaseConfig = dataBaseConfig;
    }

    /**
     * Check if a parking slot is available or not.
     * 
     * @param parkingType the vehicle type (bike or car)
     * @return int result (the id ParkingSpot) or -1 if a parking slot is
     *         unavailable
     */
    public int getNextAvailableSlot(ParkingType parkingType) {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	int result = -1;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
	    ps.setString(1, parkingType.toString());
	    rs = ps.executeQuery();
	    if (rs.next()) {
		result = rs.getInt(1);
	    }
	} catch (Exception ex) {
	    logger.error("Error fetching next available slot", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	    dataBaseConfig.closeResultSet(rs);
	}
	return result;
    }

    /**
     * Update the availability of a parking slot.
     * 
     * @param parkingSpot the parking slot to update
     * @return int updateRowCount at 1 if the parking slot updates successfully and
     *         false if the update fails
     */
    public boolean updateParking(ParkingSpot parkingSpot) {
	Connection con = null;
	PreparedStatement ps = null;
	int updateRowCount = 0;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
	    ps.setBoolean(1, parkingSpot.isAvailable());
	    ps.setInt(2, parkingSpot.getId());
	    updateRowCount = ps.executeUpdate();
	} catch (Exception ex) {
	    logger.error("Error updating parking info", ex);
	    return false;
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return (updateRowCount == 1);
    }
}