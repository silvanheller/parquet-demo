package ch.unibas.dmi.hs17.dis.storage

/**
  * Created by silvan on 10.04.17.
  */
object StorageMode extends Enumeration {
  type StorageMode = Value
  val parquet, json, csv, orc = Value

  def fromString(mode: String): FileStorage = {
    StorageMode.withName(mode) match {
      case this.parquet => ParquetLocalStorage
      case this.csv => CSVLocalStorage
      case this.json => JsonLocalStorage
      case this.orc => OrcLocalStorage
      case _ => throw new IllegalArgumentException("Mode " + mode + " not known")
    }
  }
}
