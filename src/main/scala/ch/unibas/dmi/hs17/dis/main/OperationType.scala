package ch.unibas.dmi.hs17.dis.main

/**
  * Created by silvan on 10.04.17.
  */
object OperationType extends Enumeration{
  type StorageMode = Value
  val Write, Read, Query = Value
}
