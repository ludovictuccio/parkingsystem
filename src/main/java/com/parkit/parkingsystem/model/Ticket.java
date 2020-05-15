package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

/**
 * This class is used to store ticket data.
 */
public class Ticket {
   /**
    * The parking spot number allocated to the vehicle.
    */
   private ParkingSpot parkingSpot;
   /**
    * The parking entry time.
    */
   private LocalDateTime inTime;
   /**
    * The parking exit time.
    */
   private LocalDateTime outTime;
   /**
    * The vehicle registration number.
    */
   private String vehicleRegNumber;
   /**
    * The unique ticket identifier.
    */
   private int id;
   /**
    * The price to pay during exiting process depending on parking time.
    */
   private double price;
   /**
    * Variable used to round price.
    */
   private static final double ROUNDER = 100.0;

   /**
    * Getter of id ticket.
    *
    * @return int id ticket
    */
   public int getId() {
      return id;
   }

   /**
    * Setter of id ticket.
    *
    * @param identifiant the id ticket
    */
   public void setId(final int identifiant) {
      this.id = identifiant;
   }

   /**
    * Getter of parkingSpot ticket.
    *
    * @return parkingSpot
    */
   public ParkingSpot getParkingSpot() {
      return parkingSpot;
   }

   /**
    * Setter of parkingSpot ticket.
    *
    * @param parkSpot
    */
   public void setParkingSpot(final ParkingSpot parkSpot) {
      this.parkingSpot = parkSpot;
   }

   /**
    * Getter of vehicle registration number ticket.
    *
    * @return String vehicleRegNumber, the vehicle registration number
    */
   public String getVehicleRegNumber() {
      return vehicleRegNumber;
   }

   /**
    * Setter of vehicle registration number ticket.
    *
    * @param vhlRegistration the vehicle registration number to set
    */
   public void setVehicleRegNumber(final String vhlRegistration) {
      this.vehicleRegNumber = vhlRegistration;
   }

   /**
    * Getter of ticket price.
    *
    * @return double price, the price to pay during exiting process
    */
   public double getPrice() {
      return Math.round(price * ROUNDER) / ROUNDER;
   }

   /**
    * Setter of ticket price.
    *
    * @param ticketPrice the price to set on ticket
    */
   public void setPrice(final double ticketPrice) {
      this.price = ticketPrice;
   }

   /**
    * Getter of ticket inTime.
    *
    * @return inTime the ticket entry time
    */
   public LocalDateTime getInTime() {
      return inTime;
   }

   /**
    * Setter of ticket inTime.
    *
    * @param ticketInTime the LocalDateTime of ticket entry time
    */
   public void setInTime(final LocalDateTime ticketInTime) {
      this.inTime = ticketInTime;
   }

   /**
    * Getter of ticket outTime.
    *
    * @return outTime the ticket exit time
    */
   public LocalDateTime getOutTime() {
      return outTime;
   }

   /**
    * Setter of ticket outTime.
    *
    * @param ticketOutTime the LocalDateTime of ticket exit time
    */
   public void setOutTime(final LocalDateTime ticketOutTime) {
      this.outTime = ticketOutTime;
   }
}
