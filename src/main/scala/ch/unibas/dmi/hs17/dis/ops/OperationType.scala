package ch.unibas.dmi.hs17.dis.ops

/**
  * Created by silvan on 10.04.17.
  */
object OperationType extends Enumeration{
  type OperationType = Value
  val Write, Read, Query = Value
}
