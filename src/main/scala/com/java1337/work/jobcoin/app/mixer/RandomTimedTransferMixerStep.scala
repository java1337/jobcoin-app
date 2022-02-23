package com.java1337.work.jobcoin.app.mixer
import com.java1337.work.jobcoin.app.ConfigLike
import com.java1337.work.jobcoin.app.api.TransferService
import com.java1337.work.jobcoin.app.domain.Transfer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class RandomTimedTransferMixerStep(config: ConfigLike, transferService: TransferService) extends MixerStepLike {

    private val random = new Random()
    private val minDelaySeconds = config.minimumWithdrawalTransferDelayInSeconds
    private val maxDelaySeconds = config.maximumWithdrawalTransferDelayInSeconds

    override def onTransfer(input: Transfer): Seq[Future[Transfer]] = {
        val timeMillis = getDelayTimeMillis
        Seq(
            Future {
                Thread.sleep(timeMillis)
                transferService.send(input.sourceAddress, input.destinationAddress, input.amount)
            }.flatten
        )
    }

    def getDelayTimeMillis : Int = {
        val delaySeconds = if (minDelaySeconds == maxDelaySeconds) {
            minDelaySeconds
        } else {
            val diff = maxDelaySeconds - minDelaySeconds
            random.nextInt(diff) + minDelaySeconds
        }
        1000 * delaySeconds
    }
}
