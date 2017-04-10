package ch.unibas.dmi.hs17.dis.storage

import java.io.File

import org.apache.commons.io.FileUtils

import scala.util.{Failure, Success, Try}

/**
  * Created by silvan on 10.04.17.
  */
class FileStorage extends Serializable{

  def exists(filename: String): Try[Boolean] = {
    try {
      val file = new File(filename)
      Success(file.exists())
    } catch {
      case e: Exception => Failure(e)
    }
  }


  def drop(filename: String): Try[Unit] = {
    try {
      val file = new File(filename)
      if (file.isFile) {
        val delete = file.delete()
        if (delete) return Success()
        return Failure(new Exception("Unknown failure while deleting file " + filename))
      }
      FileUtils.deleteDirectory(new File(filename))
      Success()
    } catch {
      case e: Exception => Failure(e)
    }
  }
}
