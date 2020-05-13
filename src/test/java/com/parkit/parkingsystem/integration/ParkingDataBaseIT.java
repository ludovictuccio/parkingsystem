package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
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
import com.parkit.parkingsystem.integration.database.DataBasePrepareService;
import com.parkit.parkingsystem.integration.database.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * ParkingDataBase Integration Test
 * 
 * @author Ludovic Tuccio
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static String vehicleRegNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpot parkingSpot;

    /**
     * Used to mark a half-second breakTime between two methods
     * 
     * @throws InterruptedException
     */
    private synchronized void halfSecondBreak() throws InterruptedException {
	final long HALF_SECOND = 500;
	Thread.sleep(HALF_SECOND);
    }

    @BeforeAll
    private static void setUp() throws Exception {
	parkingSpotDAO = new ParkingSpotDAO();
	ticketDAO = new TicketDAO();
	dataBasePrepareService = new DataBasePrepareService();
	parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
	dataBasePrepareService.clearDataBaseEntries(); // Clear for independents tests
	when(inputReaderUtil.readSelection()).thenReturn(1);
	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
	ticketDAO.setDataBaseConfig(dataBaseTestConfig);
    }

    @Test
    @Tag("Incoming")
    @DisplayName("Check that a ticket is actually saved in DB and Parking table is updated with availability")
    public void givenAnIncomingVehicle_whenTicketMustBeSavedtoDBAndParkingSpotAvaibilityUpdated_thenGetTicketIsNotNullAndVehiculeParkingSpotUnavailable() {

	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	parkingService.processIncomingVehicle();
	parkingSpot = parkingService.getNextParkingNumberIfAvailable();

	assertThat(ticketDAO.getTicket(vehicleRegNumber)).isNotNull(); // Check ticket saved in DB
	assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR) > 1).isTrue(); // Check vehicle parking spot
										       // unavailable
    }

    @Test
    @Tag("IncomingAndExiting")
    @DisplayName("Regular & new user - checkNumberVisitsUser - verify good visits number")
    public void givenRegularAndNewUser_whenCheckNumberVisitsUserInDB_thenReturnCorrectValue()
	    throws InterruptedException {

	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	parkingService.processIncomingVehicle();
	halfSecondBreak();
	parkingService.processExitingVehicle();
	int numberTotalVisitsRegularUser = ticketDAO.checkNumberVisitsUser(vehicleRegNumber);
	int numberTotalVisitsNewUser = ticketDAO.checkNumberVisitsUser("012345");

	assertThat(1).isEqualTo(numberTotalVisitsRegularUser); // Check that tickets are saved in DB
	assertThat(0).isEqualTo(numberTotalVisitsNewUser);
    }

    @Test
    @Tag("IncomingAndExiting")
    @DisplayName("Check that the fare generated and out time are populated correctly in the database")
    public void givenAnExitingVehicle_whenTheSystemProcessesIt_thenOutTimeShouldBeSavedToDBAndPriceAtZeroForLessThanThirtyMinutesParked()
	    throws InterruptedException {

	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	parkingService.processIncomingVehicle();
	halfSecondBreak();
	parkingService.processExitingVehicle();

	assertThat(ticketDAO.getTicket(vehicleRegNumber).getOutTime()).isNotNull();
	assertThat(ticketDAO.getTicket(vehicleRegNumber).getPrice()).isNotNull(); // Verify that exit process work
	assertThat(ticketDAO.getTicket(vehicleRegNumber).getPrice()).isEqualTo(0.0);
    }
}