package ch.unibas.dmi.hs17.dis.datagen

/**
  * A row is in the end just a sequence of strings
  * Created by silvan on 06.04.17.
  */
class Tuple(values: IndexedSeq[String]) extends Product {

  override def productElement(n: Int): String = values(n)

  override def productArity: Int = values.length

  override def canEqual(that: Any): Boolean = true
}
