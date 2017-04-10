package ch.unibas.dmi.hs17.dis.utils

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  @transient private var log_ : Logger = null

  protected def log: Logger = {
    if (log_ == null) {
      log_ = LoggerFactory.getLogger(logName)
    }
    log_
  }

  protected def logName = {
    this.getClass.getName.stripSuffix("$")
  }
}