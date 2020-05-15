package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

   private static FareCalculatorService fareCalculatorService;
   private Ticket ticket;

   private ParkingSpot parkingSpot;

   @BeforeAll
   private static void setUp() {
      fareCalculatorService = new FareCalculatorService();
   }

   @BeforeEach
   private void setUpPerTest() {
      ticket = new Ticket();
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket.setInTime(LocalDateTime.now());
      ticket.setOutTime(null);
      ticket.setParkingSpot(parkingSpot);
   }

   @Test
   @Tag("CAR")
   @DisplayName("calculate one hour fare")
   public void givenNewUserCar_whenParkOneHour_thenReturnsOneTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusHours(1));
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
   }

   @Test
   @Tag("CAR")
   @DisplayName("regular user - calculate one hour fare")
   public void givenRegularUserCar_whenParkOneHour_thenReturnsOneTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusHours(1));
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(Math.round((0.95 * Fare.CAR_RATE_PER_HOUR) * 100.0) / 100.0)
                  .isEqualTo(ticket.getPrice());
   }

   @Test
   @Tag("CAR")
   @DisplayName("45 minutes parking time should give 3/4th parking fare")
   public void givenNewUserCar_whenParkDurationBetweenThirtyAndFortyFiveminutes_thenReturnThreeQuartersOfTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(45));
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(
                  Math.round((0.75 * Fare.CAR_RATE_PER_HOUR) * 100.0) / 100.0);
   }

   @Test
   @Tag("CAR")
   @DisplayName("regular user - 45 minutes parking")
   public void givenRegularUserCar_whenParkDurationBetweenThirtyAndFortyFiveminutes_thenReturnThreeQuartersOfTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(45));
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * 0.95 * Fare.CAR_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("CAR")
   @DisplayName(" 30 minutes parking time - fare should equal to 0.75%")
   public void givenNewUserCar_whenParkforThirtyMinutesDuration_thenDiscountFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(30));
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * Fare.CAR_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("CAR")
   @DisplayName(" Regular user 30 minutes parking time - fare should equal to 0.75%")
   public void givenRegularUserCar_whenParkforThirtyMinutesDuration_thenDiscountFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(30));
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * 0.95 * Fare.CAR_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("CAR")
   @DisplayName("Less than 30 minutes parking time must be free")
   public void givenNewUserCar_whenParkforMinusThanThirtyMinutesDuration_thenReturnZeroTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(0);
   }

   @Test
   @Tag("CAR")
   @DisplayName("regular user - Less than  30 minutes parking time must be free")
   public void givenRegularUserCar_whenParkforMinusThanThirtyMinutesDuration_thenReturnZeroTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(0);
   }

   @Test
   @Tag("CAR")
   @DisplayName("24 hours parking time should give 24 * parking fare per hour")
   public void givenNewUserCar_whenParkDuringOneDay_thenReturnTwentyFourTimesFare() {
      ticket.setOutTime(ticket.getInTime().plusDays(1));
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(
                  Math.round((24 * Fare.CAR_RATE_PER_HOUR) * 100.0) / 100.0);
   }

   @Test
   @Tag("CAR")
   @DisplayName("regular user - 24 hours parking")
   public void givenRegularUserCar_whenParkDuringOneDay_thenReturnTwentyFourTimesFare() {
      ticket.setOutTime(ticket.getInTime().plusDays(1));
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(
                  Math.round(24 * 0.95 * Fare.CAR_RATE_PER_HOUR * 100.0)
                              / 100.0);
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception")
   public void givenNewUser_whenExitWithBadOutTime_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, false);
      });
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception regular user")
   public void givenRegularUser_whenExitWithBadOutTime_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, true);
      });
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception new user - null out-time")
   public void givenNewUser_whenNullOutTimeTicket_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      ticket.setOutTime(null);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, false);
      });
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception regular user - null out-time")
   public void givenRegularUser_whenNullOutTimeTicket_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      ticket.setOutTime(null);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, true);
      });
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception new user - out-time < in-time")
   public void givenNewUser_whenOutTimeIsLessThanInTimeTicket_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      ticket.setOutTime(ticket.getInTime().minusHours(1));
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, false);
      });
   }

   @Test
   @Tag("CAR")
   @DisplayName("Exception regular user - out-time < in-time")
   public void givenRegularUser_whenOutTimeIsLessThanInTimeTicket_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.CAR);
      ticket.setOutTime(ticket.getInTime().minusHours(1));
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, true);
      });
   }

   @Test
   @Tag("BIKE")
   @DisplayName("calculate one hour fare")
   public void givenNewUserBike_whenParkOneHour_thenReturnsOneTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusHours(1));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
   }

   @Test
   @Tag("BIKE")
   @DisplayName("regular user - calculate one hour fare")
   public void givenRegularUserBike_whenParkOneHour_thenReturnsOneTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusHours(1));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(0.95 * Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
   }

   @Test
   @Tag("BIKE")
   @DisplayName("45 minutes parking time should give 3/4th parking fare")
   public void givenNewUserBike_whenParkDurationBetweenThirtyAndFortyFiveminutes_thenReturnThreeQuartersOfTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(45));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
   }

   @Test
   @Tag("BIKE")
   @DisplayName("regular user - 45 minutes parking ")
   public void givenRegularUserBike_whenParkDurationBetweenThirtyAndFortyFiveminutes_thenReturnThreeQuartersOfTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(45));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * 0.95 * Fare.BIKE_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("BIKE")
   @DisplayName(" 30 minutes parking time - fare should equal to 0.75%")
   public void givenNewUserBike_whenParkforThirtyMinutesDuration_thenDiscountFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(30));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * Fare.BIKE_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("BIKE")
   @DisplayName(" Regular user 30 minutes parking time - fare should equal to 0.75%")
   public void givenRegularUserBike_whenParkforThirtyMinutesDuration_thenDiscountFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(30));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(
                  (Math.round((0.75 * 0.95 * Fare.BIKE_RATE_PER_HOUR) * 100.0)
                              / 100.0));
   }

   @Test
   @Tag("BIKE")
   @DisplayName("Less than 30 minutes parking time must be free")
   public void givenNewUserBike_whenParkforMinusThanThirtyMinutesDuration_thenReturnZeroTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, false);
      assertThat(ticket.getPrice()).isEqualTo(0);
   }

   @Test
   @Tag("BIKE")
   @DisplayName("regular user - Less than 30 minutes parking time must be free")
   public void givenRegularUserBike_whenParkforMinusThanThirtyMinutesDuration_thenReturnZeroTicketFare() {
      ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
      parkingSpot.setParkingType(ParkingType.BIKE);
      fareCalculatorService.calculateFare(ticket, true);
      assertThat(ticket.getPrice()).isEqualTo(0);
   }

   @Test
   @Tag("BIKE")
   @DisplayName("Exception")
   public void givenNewUserWithBike_whenExitWithBadOutTime_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.BIKE);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, false);
      });
   }

   @Test
   @Tag("BIKE")
   @DisplayName("Exception regular user")
   public void givenRegularUserWithBike_whenExitWithBadOutTime_thenReturnNullPointerException() {
      parkingSpot.setParkingType(ParkingType.BIKE);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, true);
      });
   }

   @Test
   @Tag("UnknowType")
   @DisplayName("UnknowType - Impossible calculation")
   public void givenUnknow() {
      ticket.setOutTime(ticket.getInTime().plusHours(1));
      parkingSpot.setParkingType(null);
      assertThatNullPointerException().isThrownBy(() -> {
         fareCalculatorService.calculateFare(ticket, false);
      });
   }
}
