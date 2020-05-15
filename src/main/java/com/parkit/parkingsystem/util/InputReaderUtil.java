package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to read int keyboard inputs, including the choice of
 * options in the main menu and the vehicle registration numbers.
 *
 * @author Ludovic Tuccio
 */
public class InputReaderUtil {
   /**
    * A Scanner initialization in order to read the inputs users.
    */
   private Scanner scan = new Scanner((System.in), "UTF-8");
   /**
    * InputReaderUtil logger.
    */
   private static final Logger LOG = LogManager.getLogger("InputReaderUtil");
   /**
    * Error message to display in catch blocks.
    */
   private static final String ERROR_MESSAGE =

               "Error while reading user input from Shell.";
   /**
    * The minimum number to enter in the menu choice selection.
    */
   private static final int MINI_INPUT_SELECTION = 1;
   /**
    * The maximum number to enter in the menu choice selection.
    */
   private static final int MAXI_INPUT_SELECTION = 3;
   /**
    * The minimum size of the vehicle registration number.
    */
   private static final int MINI_REGISTRATION_SIZE = 4;
   /**
    * The maximum size of the vehicle registration number.
    */
   private static final int MAXI_REGISTRATION_SIZE = 7;

   /**
    * InputReaderUtil scanner setter, used as InputStream Scanner for unit tests
    * in InputReaderUtilTest.class.
    *
    * @param scanner InputReaderUtil Scanner
    */
   public void setScan(final Scanner scanner) {
      this.scan = scanner;
   }

   /**
    * This method is used to read keyboard inputs in main menu. The entry must
    * be 1, 2 or 3. Other input is incorrect selection.
    *
    * @return int 1,2 or 3 for valid input or -1 if invalid input
    */
   public int readSelection() {
      try {
         int input = Integer.parseInt(scan.nextLine());

         if (input >= MINI_INPUT_SELECTION && input <= MAXI_INPUT_SELECTION) {
            return input;
         } else {
            return -1;
         }
      } catch (IllegalArgumentException e) {
         LOG.error(ERROR_MESSAGE
                     + "\r\nPlease enter valid number for proceeding further");
         return -1;
      }
   }

   /**
    * Used to read vehicle registration number inputs. The entry must be between
    * 4 and 7 characters.
    *
    * @return vehicleRegNumber String or IllegalArgumentException
    */
   public String readVehicleRegistrationNumber() {
      try {
         String vehicleRegNumber = scan.nextLine();

         if (vehicleRegNumber == null
                     || vehicleRegNumber.trim()
                                 .length() < MINI_REGISTRATION_SIZE
                     || vehicleRegNumber.trim()
                                 .length() > MAXI_REGISTRATION_SIZE
                     || !vehicleRegNumber.matches("^[0-9a-zA-Z]*$")) {
            throw new IllegalArgumentException("Invalid input provided");
         }
         return vehicleRegNumber;
      } catch (IllegalArgumentException e) {
         LOG.error(ERROR_MESSAGE
                     + "\r\nPlease enter a valid string"
                     + " for vehicle registration number");
         throw e;
      }
   }
}
