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
    @DisplayName("CAR - calculate fare")
    public void calculateFareCar() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("CAR (regular client) - calculate fare")
    public void calculateFareCarForRegularClient() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(0.95 * Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("CAR - 45 minutes parking time should give 3/4th parking fare")
    public void calculateFareCarWithLessThanOneHourParkingTime() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("CAR (regular client) - 45 minutes parking ")
    public void calculateFareCarWithLessThanOneHourParkingTimeForRegularClient() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	fareCalculatorService.calculateFare(ticket);
	assertThat(0.95 * ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("CAR - 24 hours parking time should give 24 * parking fare per hour")
    public void calculateFareCarWithMoreThanADayParkingTime() {
	ticket.setOutTime(ticket.getInTime().plusDays(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("CAR (regular client) - 24 hours parking")
    public void calculateFareCarWithMoreThanADayParkingTimeForRegularClient() {
	ticket.setOutTime(ticket.getInTime().plusDays(1));
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(24 * (0.95 * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("CAR - exception")
    public void exceptionCalculateFareCarWithFutureInTime() {
	parkingSpot.setParkingType(ParkingType.CAR);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

    @Test
    @DisplayName("BIKE - calculate fare")
    public void calculateFareBike() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("BIKE (regular client) - calculate fare")
    public void calculateFareBikeForRegularClient() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(0.95 * Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    @DisplayName("BIKE - 45 minutes parking time should give 3/4th parking fare")
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("BIKE (regular client) - 45 minutes parking ")
    public void calculateFareBikeWithLessThanOneHourParkingTimeForRegularClient() {
	ticket.setOutTime(ticket.getInTime().plusMinutes(45));
	parkingSpot.setParkingType(ParkingType.BIKE);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(0.75 * (0.95 * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("BIKE - exception")
    public void calculateFareBikeWithFutureInTime() {
	parkingSpot.setParkingType(ParkingType.BIKE);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

    @Test
    @DisplayName("UnknowType - calculate fare")
    public void calculateFareUnkownType() {
	ticket.setOutTime(ticket.getInTime().plusHours(1));
	parkingSpot.setParkingType(null);
	assertThatNullPointerException().isThrownBy(() -> {
	    fareCalculatorService.calculateFare(ticket);
	});
    }

}