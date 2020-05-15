package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class calculates the tickets price during exiting process.
 *
 * @author Ludovic Tuccio
 */
public class FareCalculatorService {
   /**
    * FareCalculatorService logger.
    */
   private static final Logger LOGGER = LogManager
               .getLogger("FareCalculatorService");
   /**
    * Out-time error message.
    */
   private static final String ERROR_MESSAGE = "Incorrect out-time provided";
   /**
    * 30 minutes threshold.
    */
   private static final int THIRTY_MINUTES = 30;
   /**
    * 45 minutes threshold.
    */
   private static final int FORTY_FIVE_MINUTES = 45;
   /**
    * Used to calculate 45 minutes fare.
    */
   private static final double FORTY_FIVE_MINUTES_FARE = 0.75;
   /**
    * To calculate discount ticket fare.
    */
   private static final double DISCOUNT_PERCENTAGE = 0.95;
   /**
    * To display discount percentage.
    */
   private static final int DISCOUNT_PERCENT_LOGGER = 5;

   /**
    * This method check errors during the exit parking process.
    *
    * @param ticket check NullPointerException
    */
   private static void checksTicketOutTimeErrors(final Ticket ticket) {
      if ((ticket.getOutTime() == null)) {
         throw new NullPointerException(ERROR_MESSAGE
                     + " NullPointerException");
      }
      if (ticket.getOutTime().compareTo(ticket.getInTime()) <= 0) {
         throw new NullPointerException(
                     ERROR_MESSAGE + ticket.getOutTime().toString());
      }
   }

   /**
    * Calculate ticket fare with different durations for new & regular users.
    *
    * @param ticket        fare
    * @param isRegularUser boolean
    */
   public void calculateFare(final Ticket ticket, final boolean isRegularUser) {

      checksTicketOutTimeErrors(ticket);

      double vehicleRatePerHour = 0;
      switch (ticket.getParkingSpot().getParkingType()) {
      case CAR:
         vehicleRatePerHour = Fare.CAR_RATE_PER_HOUR;
         break;
      case BIKE:
         vehicleRatePerHour = Fare.BIKE_RATE_PER_HOUR;
         break;
      default:
         throw new IllegalArgumentException("Unkown Parking Type");
      }

      LocalDateTime entryTime = ticket.getInTime();
      LocalDateTime exitTime = ticket.getOutTime();
      Duration durationOfTicket = Duration.between(entryTime, exitTime);
      // Less than 30 minutes is free
      LocalDateTime thirtyMinutesDuration = entryTime
                  .plusMinutes(THIRTY_MINUTES);
      // 3/4 hour threshold
      LocalDateTime threeQuartersOfAnHourDuration = entryTime
                  .plusMinutes(FORTY_FIVE_MINUTES);
      // Hours thresholds after ticket receipt
      LocalDateTime hourThreshold = entryTime.plusHours(1);

      double ticketPrice = 0;

      // Less than 30 minutes
      if (exitTime.isBefore(thirtyMinutesDuration)) {
         ticketPrice = 0;

         // Between 30 minutes and 3/4 of an hour
      } else if ((exitTime.isBefore(threeQuartersOfAnHourDuration)
                  || exitTime.isEqual(threeQuartersOfAnHourDuration))
                  && (exitTime.isAfter(thirtyMinutesDuration)
                              || exitTime.isEqual(thirtyMinutesDuration))) {
         ticketPrice = (FORTY_FIVE_MINUTES_FARE * vehicleRatePerHour);

         // Between 3/4 of an hour and 1 hour
      } else if ((exitTime.isBefore(hourThreshold)
                  || exitTime.isEqual(hourThreshold))
                  && exitTime.isAfter(threeQuartersOfAnHourDuration)) {
         ticketPrice = vehicleRatePerHour;

         // Parking time more than an hour
      } else if (exitTime.isAfter(hourThreshold)) {
         ticketPrice = durationOfTicket.toHours() * vehicleRatePerHour;
      } else {
         throw new IllegalArgumentException("An error was occured. "
                     + "Please try again in a few moments "
                     + "or contact our technical support.");
      }
      ticket.setPrice(ticketPrice);

      // Set the ticket price with a 5% discount for regular users
      if (isRegularUser) {
         ticket.setPrice(DISCOUNT_PERCENTAGE * ticket.getPrice());
         LOGGER.info("As regular user you benefit from a {}% discount",
                     DISCOUNT_PERCENT_LOGGER);
      }
   }
}
