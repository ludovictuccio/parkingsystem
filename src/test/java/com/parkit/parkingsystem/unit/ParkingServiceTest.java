package com.parkit.parkingsystem.unit;

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
public class ParkingServiceTest {

   private static ParkingService parkingService;
   private static Ticket ticket;
   private static ParkingSpot parkingSpot;
   private static LocalDateTime inTime;
   private static String vehicleRegNumber = "ABCDEF";

   @Mock
   private static InputReaderUtil inputReaderUtil;
   @Mock
   private static ParkingSpotDAO parkingSpotDAO;
   @Mock
   private static TicketDAO ticketDAO;
   @Mock
   private static FareCalculatorService fareCalculatorService;

   @BeforeEach
   private void setUpPerTest() {
      // To recover a parking out-time of one day
      inTime = LocalDateTime.now().minusHours(24).minusMinutes(1);
      try {
         when(inputReaderUtil.readVehicleRegistrationNumber())
                     .thenReturn(vehicleRegNumber);
         when(parkingSpotDAO.updateParking(any(ParkingSpot.class)))
                     .thenReturn(true);
         parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
                     ticketDAO);
         parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
         ticket = new Ticket();
         ticket.setInTime(inTime);
         ticket.setParkingSpot(parkingSpot);
         ticket.setVehicleRegNumber(vehicleRegNumber);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Failed to set up test mock objects");
      }
   }

   @Test
   @Tag("Incoming")
   @DisplayName("Incoming new CAR vehicle ")
   public void givenCarEntry_whenProcessIncomingVehicle_thenCheckGoodGenerationOfTicket() {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                  .thenReturn(1);

      parkingService.processIncomingVehicle();

      verify(inputReaderUtil, times(1)).readSelection();
      verify(parkingSpotDAO, times(1))
                  .getNextAvailableSlot(any(ParkingType.class));
      verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
      verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, times(1)).getTicket(anyString());
   }

   @Test
   @Tag("Incoming")
   @DisplayName("Incoming regular CAR user ")
   public void givenCarEntry_whenRegularUser_thenCheckGoodTotalVisitsNumberOfTickets() {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                  .thenReturn(1);
      when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(1);

      parkingService.processIncomingVehicle();

      verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
   }

   @Test
   @Tag("Incoming")
   @DisplayName("Incoming new BIKE vehicle ")
   public void givenBikeEntry_whenProcessIncomingVehicle_thenCheckGoodGenerationOfTicket() {
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                  .thenReturn(1);

      parkingService.processIncomingVehicle();

      verify(inputReaderUtil, times(1)).readSelection();
      verify(parkingSpotDAO, times(1))
                  .getNextAvailableSlot(any(ParkingType.class));
      verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
      verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, times(1)).getTicket(anyString());
   }

   @Test
   @Tag("Incoming")
   @DisplayName("Incoming regular BIKE user ")
   public void givenBikeEntry_whenRegularUser_thenCheckGoodTotalVisitsNumberOfTickets() {
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                  .thenReturn(1);
      when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(1);

      parkingService.processIncomingVehicle();

      verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
   }

   @Test
   @Tag("Exiting")
   @DisplayName("Exiting vehicle - ParkingSpot updated")
   public void givenParkedCar_whenExiting_thenParkingSpotMustBeUpdated() {
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

      parkingService.processExitingVehicle();

      verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
      verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
      verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
   }

   @Test
   @Tag("Exiting")
   @DisplayName("Exiting vehicle - regular user")
   public void givenCarEntry_whenRegularUser_thenCheckGoodTotalVisitsNumberOfTickes() {
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(2);

      parkingService.processExitingVehicle();

      verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());

   }

   @Test
   @Tag("Exiting")
   @DisplayName("Exiting vehicle - ticket fare for new user")
   public void givenNewUser_whenExitingParking_thenReturnTicketFareWithoutDiscount() {
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(0);

      int numberVisitsUser = ticketDAO
                  .checkNumberVisitsUser(ticket.getVehicleRegNumber());
      boolean isRegularUser = numberVisitsUser >= 1;
      fareCalculatorService.calculateFare(ticket, isRegularUser);
      parkingService.processExitingVehicle();

      verify(fareCalculatorService).calculateFare(ticket, false);
   }

   @Test
   @Tag("Exiting")
   @DisplayName("Exiting vehicle - ticket fare for regular user")
   public void givenNewUser_whenExitingParking_thenReturnTicketFareWithFivePercentDiscount() {
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(2);

      int numberVisitsUser = ticketDAO
                  .checkNumberVisitsUser(ticket.getVehicleRegNumber());
      boolean isRegularUser = numberVisitsUser >= 1;
      fareCalculatorService.calculateFare(ticket, isRegularUser);
      parkingService.processExitingVehicle();

      verify(fareCalculatorService).calculateFare(ticket, true);
   }
}