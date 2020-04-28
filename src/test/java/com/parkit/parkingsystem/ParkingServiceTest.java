package com.parkit.parkingsystem;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

//    @BeforeEach
//    private void setUpPerTest() {
//	try {
//	    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
//
//	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
//	    Ticket ticket = new Ticket();
//	    ticket.setInTime(LocalDateTime.of(2020, 10, 12, 15, 30));
//	    ticket.setParkingSpot(parkingSpot);
//	    ticket.setVehicleRegNumber("ABCDEF");
//	    when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
//	    when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
//	    when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
//	    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//	} catch (Exception e) {
//	    e.printStackTrace();
//	    throw new RuntimeException("Failed to set up test mock objects");
//	}
//    }
//
//    @DisplayName("Incoming vehicule")
//    @Test
//    public void processIncomingVehicle() {
//
//    }
//
//    @DisplayName("Incoming vehicule (Regular client) notify 5% discount")
//    @Test
//    public void processIncomingVehicleForRegularClient() {
//
//    }
//
//    @DisplayName("Exiting vehicule")
//    @Test
//    public void processExitingVehicleTest() {
//	parkingService.processExitingVehicle();
//	verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
//    }
//
//    @DisplayName("Exiting vehicule (Regular client) with 5% discount")
//    @Test
//    public void processExitingVehicleTestForRegularClient() {
//
//    }

}
