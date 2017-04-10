package ch.unibas.dmi.hs17.dis.utils

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Define your headers in names, then throw your values (with a toString() preferably) in the write() method
  *
  * Created by silvan on 08.07.16.
  */
object EvaluationResultLogger {

  /** Everything that gets logged */
  private val names = Seq("rows", "cols", "stringlen", "storageMode", "operation", "time")

  new File("results").mkdir()
  private val out = new PrintWriter(new BufferedWriter(new FileWriter("results/results_" + new SimpleDateFormat("MMdd_HHmm").format(Calendar.getInstance.getTime) + ".tsv", true)))
  private val seperator = "\t"
  out.println(names.mkString(seperator))
  out.flush()

  def write(values: Map[String, Any]) = {
    out.println(names.map(values(_)).mkString(seperator))
    out.flush()
  }
}
