package com.parkit.parkingsystem.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * This class is used to manage parking entry and exit process.
 *
 * @author Ludovic Tuccio
 */
public class ParkingService {
   /**
    * Create a FareCalculatorService object.
    */
   private static FareCalculatorService fareCalculatorService

               = new FareCalculatorService();
   /**
    * InputReaderUtil object.
    */
   private InputReaderUtil inputReaderUtil;
   /**
    * ParkingSpotDAO object.
    */
   private ParkingSpotDAO parkingSpotDAO;
   /**
    * TicketDAO object.
    */
   private TicketDAO ticketDAO;
   /**
    * ParkingService logger.
    */
   private static final Logger LOGGER = LogManager.getLogger("ParkingService");
   /**
    * Error message in incoming process. Vehicle already parked.
    */
   private static final String ERROR_PARKED_VHL =

               "INVALID ENTRY \r\n This registration is already occupied at "
                           + "a parking space\r\n Enter a valid registration "
                           + "(or other character to exit and return to menu).";
   /**
    * Error message in exiting process. Vehicle already exited.
    */
   private static final String ERROR_EXITED_VHL =

               "INVALID ENTRY \r\nThis registration has already exited the "
                           + "parking. \r\n Enter a valid registration "
                           + "(or other character to exit and return to menu).";
   /**
    * Formats the time for a careful display.
    */
   private static DateTimeFormatter formatter = DateTimeFormatter
               .ofPattern("dd/MM/yyyy HH:mm:ss");
   /**
    * Defines visits threshold to be a regular user.
    */
   private static final int VISITS_THRESHOLD_REGULAR_USER = 1;
   /**
    * To display discount percentage.
    */
   private static final int DISCOUNT_PERCENT_LOGGER = 5;

   /**
    * Class constructor.
    *
    * @param inputReader
    * @param daoParkingSpot
    * @param daoTicket
    */
   public ParkingService(final InputReaderUtil inputReader,
               final ParkingSpotDAO daoParkingSpot, final TicketDAO daoTicket) {
      this.inputReaderUtil = inputReader;
      this.parkingSpotDAO = daoParkingSpot;
      this.ticketDAO = daoTicket;
   }

   /**
    * This method manages incoming vehicle.
    */
   public void processIncomingVehicle() {
      try {
         ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
         if (parkingSpot != null && parkingSpot.getId() > 0) {
            String vehicleRegNumber = getVehicleRegNumber();

            // Check if a vehicle already has an in-ticket and check if his
            // out-ticket is
            // null to know if he's already parked
            Ticket parkedVehicle = ticketDAO.getTicket(vehicleRegNumber);
            // While a vehicle has already had an in-ticket and its out-ticket
            // has not been
            // validated (while the vehicle is already parked)
            while (parkedVehicle != null
                        && parkedVehicle.getOutTime() == null) {
               LOGGER.error(ERROR_PARKED_VHL);
               vehicleRegNumber = getVehicleRegNumber();
               parkedVehicle = ticketDAO.getTicket(vehicleRegNumber);
            }
            parkingSpot.setAvailable(false);
            parkingSpotDAO.updateParking(parkingSpot);
            LocalDateTime inTime = LocalDateTime.now();
            String inTimeFormatter = inTime.format(formatter);
            Ticket ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(vehicleRegNumber);
            ticket.setPrice(0);
            ticket.setInTime(inTime);
            ticket.setOutTime(null);
            ticketDAO.saveTicket(ticket);
            int numberVisitsUser = ticketDAO
                        .checkNumberVisitsUser(ticket.getVehicleRegNumber());

            // Check if a user is considered regular with a fixed number of
            // visits by
            // checking its total number of tickets
            if (numberVisitsUser >= VISITS_THRESHOLD_REGULAR_USER) {
               LOGGER.info("As regular user, you will benefit from a {}% "
                           + "discount on your final fare",
                           DISCOUNT_PERCENT_LOGGER);
            }
            LOGGER.info("Generated Ticket and saved in DB");
            LOGGER.info("Please park your vehicle in spot number: {}",
                        parkingSpot.getId());
            LOGGER.info("Recorded in-time for vehicle number: {} is: {}",
                        vehicleRegNumber, inTimeFormatter);
         } else {
            LOGGER.error("Sorry the parking is full."
                        + " No more places are available.");
         }
      } catch (Exception e) {
         LOGGER.error("Unable to process incoming vehicle");
      }
   }

   /**
    * This method calls readVehicleRegistrationNumber method from
    * InputReaderUtil.class to enter valid registration.
    *
    * @return readVehicleRegistrationNumber() the String vehicle registration
    *         number
    */
   private String getVehicleRegNumber() {
      LOGGER.info("Please type the vehicle registration number "
                  + "and press enter key");
      return inputReaderUtil.readVehicleRegistrationNumber();
   }

   /**
    * This method check if there is any available parking spot for incoming
    * users.
    *
    * @return parkingSpot, the available parking spot
    */
   public ParkingSpot getNextParkingNumberIfAvailable() {
      int parkingNumber = 0;
      ParkingSpot parkingSpot = null;
      try {
         ParkingType parkingType = getVehicleType();
         parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
         if (parkingNumber > 0) {
            parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
         } else {
            throw new SQLException("Error fetching parking number from DB."
                        + " Parking slots might be full");
         }
      } catch (IllegalArgumentException ie) {
         LOGGER.error("Error parsing user input for type of vehicle", ie);
      } catch (Exception e) {
         LOGGER.error("Error fetching next available parking slot", e);
      }
      return parkingSpot;
   }

   /**
    * Ask the user what type of vehicle he wants to park (car/bike) during
    * incoming process.
    *
    * @return ParkingType enumeration the selected vehicle type
    *
    */
   private ParkingType getVehicleType() {
      LOGGER.info("Please select vehicle type from menu");
      LOGGER.info("1 CAR");
      LOGGER.info("2 BIKE");
      int input = inputReaderUtil.readSelection();
      switch (input) {
      case 1:
         return ParkingType.CAR;
      case 2:
         return ParkingType.BIKE;
      default:
         LOGGER.error("Incorrect input provided");
         throw new IllegalArgumentException("Entered input is invalid");
      }
   }

   /**
    * This method manages exiting vehicle.
    */
   public void processExitingVehicle() {
      try {
         String vehicleRegNumber = getVehicleRegNumber();
         Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

         // To not recover an already paid vehicle ticket and who has left the
         // parking
         while (ticket.getOutTime() != null) {
            LOGGER.error(ERROR_EXITED_VHL);
            vehicleRegNumber = getVehicleRegNumber();
            ticket = ticketDAO.getTicket(vehicleRegNumber);
         }

         LocalDateTime inTime = ticket.getInTime();
         String inTimeFormatter = inTime.format(formatter);
         LocalDateTime outTime = LocalDateTime.now();
         String outTimeFormatter = outTime.format(formatter);
         ticket.setOutTime(outTime);
         int numberVisitsUser = ticketDAO
                     .checkNumberVisitsUser(ticket.getVehicleRegNumber());
         fareCalculatorService.calculateFare(ticket,
                     numberVisitsUser >= VISITS_THRESHOLD_REGULAR_USER);

         if (ticketDAO.updateTicket(ticket)) {
            ParkingSpot parkingSpot = ticket.getParkingSpot();
            parkingSpot.setAvailable(true);
            parkingSpotDAO.updateParking(parkingSpot);
            DecimalFormat roundedPrice = new DecimalFormat("#0.00 €");
            String finalTicketPriceRounded = roundedPrice
                        .format(ticket.getPrice());
            LOGGER.info("Recorded in-time : {}", inTimeFormatter);
            LOGGER.info("Please pay the parking fare: {}",
                        finalTicketPriceRounded);
            LOGGER.info("Recorded out-time for vehicle number: {} is: {}",
                        ticket.getVehicleRegNumber(), outTimeFormatter);
         } else {
            LOGGER.error(
                        "Unable to update ticket information. Error occurred.");
         }
      } catch (Exception e) {
         LOGGER.error("Unable to process exiting vehicle."
                     + " Please verify the entry vehicle registration number.");
      }
   }
}
