package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * This class is used to display the application main menu.
 */
public final class InteractiveShell {
   /**
    * InteractiveShell logger.
    */
   private static final Logger LOG = LogManager.getLogger("InteractiveShell");
   /**
    * An int for exit option to avoid Magic Number.
    */
   private static final int EXIT_OPTION = 3;

   /**
    * Empty InteractiveShell constructor.
    */
   private InteractiveShell() {

   }

   /**
    * This display method manages the main menu of the application. Users can
    * choose between enter or exit a vehicle from the parking.
    */
   public static void loadInterface() {

      LOG.info("Welcome to Parking System!");

      boolean continueApp = true;
      InputReaderUtil inputReaderUtil = new InputReaderUtil();
      ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
      TicketDAO ticketDAO = new TicketDAO();
      ParkingService parkingService = new ParkingService(inputReaderUtil,
                  parkingSpotDAO, ticketDAO);

      while (continueApp) {
         loadMenu();
         int option = inputReaderUtil.readSelection();
         switch (option) {
         case 1:
            parkingService.processIncomingVehicle();
            break;
         case 2:
            parkingService.processExitingVehicle();
            break;
         case EXIT_OPTION:
            LOG.info("Exiting from the system!");
            continueApp = false;
            break;
         default:
            LOG.info("Unsupported option. Please enter a "
                        + "number corresponding to the provided menu");
         }
      }
   }

   /**
    * This method display the main menu.
    */
   private static void loadMenu() {
      LOG.info("Please select an option."
                  + " Simply enter the number to choose an action");
      LOG.info("1 New Vehicle Entering - Allocate Parking Space");
      LOG.info("2 Vehicle Exiting - Generate Ticket Price");
      LOG.info("3 Shutdown System");
   }
}
