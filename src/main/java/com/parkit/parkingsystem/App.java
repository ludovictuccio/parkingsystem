package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;

/**
 * Launch Park'it Application.
 */
final class App {

   /**
    * This method main launch Park'it application.
    *
    * @param args the method main
    *
    */
   public static void main(final String[] args) {

      InteractiveShell.loadInterface();
   }

   private App() {
   }
}
