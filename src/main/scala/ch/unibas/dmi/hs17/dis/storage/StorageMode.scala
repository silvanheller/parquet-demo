package ch.unibas.dmi.hs17.dis.storage

/**
  * Created by silvan on 10.04.17.
  */
object StorageMode extends Enumeration {
  type StorageMode = Value
  val Parquet, JSON, CSV, ORC = Value
}
