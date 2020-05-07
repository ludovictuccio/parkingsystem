package com.parkit.parkingsystem.constants;

/**
 * This class contains any MySQL constants that allows exchange between the
 * application and the different tables.
 * 
 * @author Ludovic Tuccio
 */
public final class DBConstants {

    /**
     * Empty class constructor.
     */
    private DBConstants() {

    }

    /*
     * Constant that is used to search for available parking spots.
     * 
     * @see ParkingSpotDAO
     */
    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    /*
     * Constant that is used to update parking spots availability.
     * 
     * @see ParkingSpotDAO
     */
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    /*
     * Constant that is used to save ticket in database.
     * 
     * @see TicketDAO
     */
    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    /*
     * Constant that is used to update a database ticket.
     * 
     * @see TicketDAO
     */
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    /*
     * Constant that is used to get a database ticket.
     * 
     * @see TicketDAO
     */
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC limit 1";
    /*
     * Constant that is used to check on database the total visits number of the
     * same vehicle in the parking.
     * 
     * @see TicketDAO
     */
    public static final String CHECK_EXISTING_OLD_TICKETS = "select count(*) as numberVisitsUser from ticket where VEHICLE_REG_NUMBER=? and OUT_TIME is not null";
}
