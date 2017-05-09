package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.utils.Logging

/**
  * Created by silvan on 07.05.17.
  */
class Person(mother: Person, father: Person, name: String, age: Int) extends Product4[Person, Person, String, Int] with Serializable with Logging{

  log.warn("CREATING PERSON")

  def getName: String = name

  def getAge: Int = age

  /**
    * override def productElement(n: Int): Any = {
    * n match {
    * case 1 => mother
    * case 2 => father
    * case 3 => name
    * case 4 => age
    * }
    * }
    * *
    * override def productArity: Int = 4
    */

  override def canEqual(that: Any): Boolean = {
    that match {
      case person: Person =>
        if (person.getName.equals(this.name) && person.getAge == this.age) {
          true
        } else {
          false
        }
      case _ =>
        false
    }
  }

  override def _1: Person = mother

  override def _2: Person = father

  override def _3: String = name

  override def _4: Int = age
}
