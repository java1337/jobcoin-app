package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.api.TransferService
import com.java1337.work.jobcoin.app.mixer.{ConfiguredDepositReceivingMixerStep, DistributingMixer}

import java.lang.Thread.sleep
import scala.util.Try

class PollingWorkThread(
    transferService: TransferService,
    configuredDepositReceivingMixerStep: ConfiguredDepositReceivingMixerStep,
    distributingMixer: DistributingMixer
) extends Thread {

    private val SLEEP_TIME_MILLIS = 5000

    override def run(): Unit = {
        while (true) {
            sleep(SLEEP_TIME_MILLIS)
            val newTransactions = transferService.poll()
            Try(newTransactions.foreach(configuredDepositReceivingMixerStep.onTransfer))
            Try(newTransactions.foreach(distributingMixer.onTransfer))
        }
    }
}
