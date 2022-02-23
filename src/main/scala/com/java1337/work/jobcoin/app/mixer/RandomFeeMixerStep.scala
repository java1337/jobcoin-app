package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.ConfigLike
import com.java1337.work.jobcoin.app.domain.Transfer

import scala.concurrent.Future
import scala.util.Random

class RandomFeeMixerStep(config: ConfigLike) extends MixerStepLike {

    val random = new Random()

    override def onTransfer(input: Transfer): Seq[Future[Transfer]] = {
        val feePercentage: Int = getFeePercentage
        val remainingAmount: BigDecimal = (100.0 - feePercentage) / 100.0
        Seq(Future.successful(input.copy(amount = input.amount * remainingAmount)))
    }

    def getFeePercentage: Int = {
        val randomFeeRange: Int = config.maximumFeePercentage - config.minimumFeePercentage
        if (randomFeeRange == 0) {
            config.minimumFeePercentage
        } else {
            config.minimumFeePercentage + random.nextInt(randomFeeRange)
        }
    }
}
