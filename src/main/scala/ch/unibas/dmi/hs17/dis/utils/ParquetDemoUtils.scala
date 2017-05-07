package ch.unibas.dmi.hs17.dis.utils

import java.util.Scanner

/**
  * Created by silvan on 07.05.17.
  */
trait ParquetDemoUtils extends Logging {

  def verifyInput(action: String): Unit = {
    val keyboard = new Scanner(System.in)
    System.out.println("enter 42 to confirm "+action)
    val myint = keyboard.nextInt
    if (myint != 42) {
      log.warn("Aborting {}", action)
      System.exit(1)
    }
  }

}
