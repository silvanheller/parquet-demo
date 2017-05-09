package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.datagen.Generator
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging, ParquetDemoUtils}

/**
  * Created by silvan on 07.05.17.
  */
class PersonWriteOp(rows: Seq[Int], stringlens: Seq[Int]) extends Config with Logging with ParquetDemoUtils {

  def execute()(implicit ac: AppContext): Unit = {
    verifyInput("person-writing")
    StorageMode.values.foreach(storageMode => {
      log.debug("====================================")
      log.debug("Evaluating Person-writing: Storage Mode {}\n", storageMode)
      log.debug("====================================")
      //Cache current storage method
      Generator.genAndWriteStringDF(rows = 10, cols = 10, stringLength = 10, filepath = LOCAL_DATAPATH + "toy_data." + storageMode.toString, storageMode = storageMode)


      rows.foreach(_row => {
        stringlens.foreach(_stringlen => {
          if (storageMode.id != StorageMode.csv.id) {
            val start = System.currentTimeMillis()
            val write = Generator.genAndWritePersonDF(_row, _stringlen, LOCAL_DATAPATH + PersonWriteOp.getFileName(_row, _stringlen, storageMode), storageMode)
            val stop = System.currentTimeMillis()
            val time = {
              if (write.isSuccess) {
                stop - start
              } else
                0
            }
            log.debug("time: " + time + " sm: " + storageMode)
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> -1, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.WriteNested, "time" -> time))
          }
        })
      })
    })
  }

}

object PersonWriteOp {
  def getFileName(_row: Int, stringlen: Int, storageMode: StorageMode.Value): Any = {
    _row + "_person_" + stringlen + "." + storageMode.toString
  }
}