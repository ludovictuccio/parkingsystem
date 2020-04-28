package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("CAR - calculate one hour fare")
    public void calculateFareforCar_forOneHour_thenReturnsOnceTheTicketFare() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("CAR (regular user) - calculate one hour fare")
    public void calculateFareCarForRegularUser_forOneHour_thenReturnsFivePercentDiscount() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	fareCalculatorService.calculateFareForRegularClient(ticket);
	assertThat(0.95 * Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("CAR - 45 minutes parking time should give 3/4th parking fare")
    public void calculateFareCar_forPeriodBetweenThirtyAndFortyFiveminutes_returnsThreeQuartersOfFare() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("CAR (regular user) - 45 minutes parking ")
    public void calculateFareCarForRegularUser_forPeriodBetweenThirtyAndFortyFiveminutes_returnsThreeQuartersReducedFare() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	fareCalculatorService.calculateFareForRegularClient(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * (0.95 * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("CAR - 30 minutes parking time must be free ")
    public void calculateFareCar_forMinusThanThirtyMinutes_returnsZero() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
	fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("CAR - (regular user) 30 minutes parking time must be free ")
    public void calculateFareCarForRegularUser_forMinusThanThirtyMinutes_returnsZero() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
	fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("CAR - 24 hours parking time should give 24 * parking fare per hour")
    public void calculateFareCar_forADayParkingTime_returnsTwentyFourTimesFare() {
	ticket.setOutTime(ticket.getInTime().plusDays(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("CAR (regular user) - 24 hours parking")
    public void calculateFareCarForRegularUser_forADayParkingTime_returnsTwentyFourTimesReducedFare() {
	ticket.setOutTime(ticket.getInTime().plusDays(1));
	fareCalculatorService.calculateFareForRegularClient(ticket);
	assertThat(ticket.getPrice()).isEqualTo(24 * (0.95 * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("CAR - exception")
    public void givenNullPointerException_whenDetectedForCar_isThrown() {
	parkingSpot.setParkingType(ParkingType.CAR);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

    @Test
    @DisplayName("BIKE - calculate one hour fare")
    public void calculateFareforBike_forOneHour_thenReturnsOnceTheTicketFare() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("BIKE (regular user) - calculate one hour fare")
    public void calculateFareBikeForRegularUser_forOneHour_thenReturnsFivePercentDiscount() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFareForRegularClient(ticket);
	assertThat(0.95 * Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("BIKE - 45 minutes parking time should give 3/4th parking fare")
    public void calculateFareBike_forPeriodBetweenThirtyAndFortyFiveminutes_returnsThreeQuartersOfFare() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("BIKE (regular user) - 45 minutes parking ")
    public void calculateFareBikeForRegularUser_forPeriodBetweenThirtyAndFortyFiveminutes_returnsThreeQuartersReducedFare() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFareForRegularClient(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * (0.95 * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("BIKE - 30 minutes parking time must be free")
    public void calculateFareBike_forMinusThanThirtyMinutes_returnsZero() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("BIKE - (regular user)  30 minutes parking time must be free")
    public void calculateFareBikeForRegularUser_forMinusThanThirtyMinutes_returnsZero() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(29).plusSeconds(59));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("BIKE - exception")
    public void givenNullPointerException_whenDetectedForBike_isThrown() {
	parkingSpot.setParkingType(ParkingType.BIKE);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

    @Test
    @DisplayName("UnknowType -  impossible calculation")
    public void givenUnknowTypeVehicle_whenTicketFareIsRequested_thenReturnException() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(null);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

}