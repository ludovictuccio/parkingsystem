package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * This class permits the storage and retrieving values from the MySQL database
 * parking table.
 */
public class ParkingSpot {
   /**
    * The parking number.
    */
   private int number;
   /**
    * ParkingType that can be used by cars or bikes.
    */
   private ParkingType parkingType;
   /**
    * The parking availability.
    */
   private boolean isAvailable;

   /**
    * Class constructor.
    *
    * @param nb        the unique identifier of a parking spot
    * @param parkType  the parking type (car or bike)
    * @param available will define if a parking spot is available or not
    */
   public ParkingSpot(final int nb, final ParkingType parkType,
               final boolean available) {
      this.number = nb;
      this.parkingType = parkType;
      this.isAvailable = available;
   }

   /**
    * Getter of parkingSpot number.
    *
    * @return number the int id parkingSpot
    */
   public int getId() {
      return number;
   }

   /**
    * Setter of parkingSpot number.
    *
    * @param nb the parkingSpot id
    */
   public void setId(final int nb) {
      this.number = nb;
   }

   /**
    * Getter of the parkingType.
    *
    * @return a ParkingType, CAR or BIKE
    */
   public ParkingType getParkingType() {
      return parkingType;
   }

   /**
    * Setter of the parkingType.
    *
    * @param parkType CAR or BIKE
    */
   public void setParkingType(final ParkingType parkType) {
      this.parkingType = parkType;
   }

   /**
    * Getter of parkingSpot isAvailable.
    *
    * @return boolean which will define if a parking spot is available or not
    */
   public boolean isAvailable() {
      return isAvailable;
   }

   /**
    * Setter of parkingSpot isAvailable.
    *
    * @param available true if a parking spot is available, else false
    */
   public void setAvailable(final boolean available) {
      isAvailable = available;
   }

   /**
    * Boolean equals method.
    *
    * @param o an equals Object
    * @return boolean true if number has the same value of the attribute number
    *         else false
    */
   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      ParkingSpot that = (ParkingSpot) o;
      return number == that.number;
   }

   /**
    * int hashCode method.
    *
    * @return number hashCode
    */
   @Override
   public int hashCode() {
      return number;
   }
}
