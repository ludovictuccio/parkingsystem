package com.parkit.parkingsystem.constants;

/**
 * This class contains all SQL queries that allows exchange between the
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

   /**
    * SQL query used to search for available parking spots.
    **/
   public static final String GET_NEXT_PARKING_SPOT =

               "select min(PARKING_NUMBER) from parking "
                           + "where AVAILABLE = true and TYPE = ?";

   /**
    * SQL query used to update parking spots availability.
    **/
   public static final String UPDATE_PARKING_SPOT =

               "update parking set available = ? where PARKING_NUMBER = ?";

   /**
    * SQL query used to save ticket in database.
    **/
   public static final String SAVE_TICKET =

               "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER,"
                           + " PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";

   /**
    * SQL query used to update a database ticket.
    **/
   public static final String UPDATE_TICKET =

               "update ticket set PRICE=?, OUT_TIME=? where ID=?";

   /**
    * SQL query used to get a database ticket.
    **/
   public static final String GET_TICKET =

               "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME,"
                           + " p.TYPE from ticket t,parking p where "
                           + "p.parking_number = t.parking_number and "
                           + "t.VEHICLE_REG_NUMBER=? order by t.IN_TIME "
                           + "DESC limit 1";

   /**
    * SQL query used to check on DB the total visits number of the same vehicle
    * in the parking.
    **/
   public static final String CHECK_EXISTING_OLD_TICKETS =

               "select count(*) as numberVisitsUser from ticket where "
                           + "VEHICLE_REG_NUMBER=? and OUT_TIME is not null";
}
