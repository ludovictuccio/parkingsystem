package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingServiceTest {

    private static ParkingService parkingService;
    private static InputReaderUtil inputReaderUtil;
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private Ticket ticket;
    private ParkingSpot parkingSpot;

    @BeforeAll
    private static void setUp() {
	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @BeforeEach
    private void setUpPerTest() {
	parkingSpotDAO = new ParkingSpotDAO();
	ticket = new Ticket();
	ticketDAO = new TicketDAO();
	parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	ticket.setParkingSpot(parkingSpot);
	ticket.setVehicleRegNumber("ABCDEF");
    }

    @Tag("processIncomingVehicle")
    @Test
    @DisplayName("processIncomingVehicle - Vehicle Already Parked")
    public void givenIncomingVehicle_whenVehicleIsAlreadyParked_thenReturnOne() {

	String vehicleRegNumber1 = "ABCDEF";
	vehicleRegNumber1 = ticket.getVehicleRegNumber();

	ticketDAO.checkIfVehicleIsAlreadyParked(vehicleRegNumber1);
	parkingService.processIncomingVehicle();

	assertThat(ticketDAO.checkIfVehicleIsAlreadyParked(ticket.getVehicleRegNumber())).isEqualTo(1);
    }

    @Tag("processIncomingVehicle")
    @Test
    @DisplayName("processIncomingVehicle - Vehicle Not Already Parked")
    public void givenIncomingVehicle_whenVehicleIsNotAlreadyParked_thenReturnExitingProcess() {

	String vehicleRegNumber1 = "AB000EF";
	vehicleRegNumber1 = ticket.getVehicleRegNumber();

	ticketDAO.checkIfVehicleIsAlreadyParked(vehicleRegNumber1);
	parkingService.processIncomingVehicle();

	assertThat(ticketDAO.saveTicket(ticket)).isFalse();
    }

    @Tag("processExitingVehicle")
    @Test
    @DisplayName("processExitingVehicle - Vehicle Already Parked")
    public void givenExitingVehicle_whenVehicleIsAlreadyParked_thenReturnUpdateParkingTrue() {

	parkingService.processExitingVehicle();

	assertThat(parkingSpotDAO.updateParking(parkingSpot)).isTrue();
    }
}