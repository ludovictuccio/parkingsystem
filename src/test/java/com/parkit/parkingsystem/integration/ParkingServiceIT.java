package com.parkit.parkingsystem.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceIT {

    private static ParkingService parkingService;
    private Ticket ticket;
    private ParkingSpot parkingSpot;
    private LocalDateTime inTime;

    @Mock
    private FareCalculatorService fareCalculatorService;
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
	inTime = LocalDateTime.now().minusHours(1).minusMinutes(1); // To recover a parking out-time of one hour
	try {
	    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
	    when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
	    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	    ticket = new Ticket();
	    ticket.setInTime(inTime);
	    ticket.setParkingSpot(parkingSpot);
	    ticket.setVehicleRegNumber("ABCDEF");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Failed to set up test mock objects");
	}
    }

    @Test
    @Tag("Incoming")
    @DisplayName("Incoming vehicle - new vehicle")
    public void givenCarEntry_whenProcessIncomingVehicle_thenCheckGoodGenerationOfTicket() {
	when(inputReaderUtil.readSelection()).thenReturn(1);
	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

	parkingService.processIncomingVehicle();

	verify(inputReaderUtil, times(1)).readSelection();
	verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
	verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
	verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
	verify(ticketDAO, times(1)).getTicket(anyString());
    }

    @Test
    @Tag("Exiting")
    @DisplayName("Exiting vehicle - ParkingSpot updated")
    public void givenParkedCar_whenExiting_thenParkingSpotMustBeUpdated() {
	when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

	parkingService.processExitingVehicle();

	verify(ticketDAO, times(1)).getTicket(anyString());
	verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
	verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
	verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

}