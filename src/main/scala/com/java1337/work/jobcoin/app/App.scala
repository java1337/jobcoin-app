package com.java1337.work.jobcoin.app

import java.util.logging.Logger


object App {

    private val logger = Logger.getLogger("com.java1337.work.jobcoin.app.App$")

    def main(args : Array[String]): Unit = {
        logger.info("Starting app")
        Wiring.pollingWorkThread.start()
        logger.info("App started")
    }
}
