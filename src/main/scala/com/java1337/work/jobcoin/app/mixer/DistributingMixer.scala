package com.java1337.work.jobcoin.app.mixer
import com.java1337.work.jobcoin.app.domain.Transfer

import java.util.logging.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DistributingMixer(splitter: AccountSplittingMixerStep, randomFeeMixerStep: RandomFeeMixerStep, randomTimedTransferMixer: RandomTimedTransferMixerStep) extends MixerStepLike {

    private val logger: Logger = Logger.getLogger(getClass.getName)

    override def onTransfer(input: Transfer): Seq[Future[Transfer]] = {
        var returnMe : Seq[Future[Transfer]] = Seq(Future.failed(new RuntimeException(s"Unable to distribute transfer from ${input.sourceAddress} to ${input.destinationAddress} of amount ${input.amount}")))

        val splitTransferFutures: Future[Seq[Transfer]] = Future.sequence(splitter.onTransfer(input))
        splitTransferFutures.onComplete { splitTransfers: Try[Seq[Transfer]] =>
            val randomFeeFutures: Future[Seq[Transfer]] = splitTransfers match {
                case Failure(exception) =>
                    logger.severe(s"Unable to split transfer $input - ${exception.getMessage}")
                    Future.failed(exception)
                case Success(value) => Future.sequence(value.flatMap(randomFeeMixerStep.onTransfer))
            }
            randomFeeFutures.onComplete { randomFeeTransfers: Try[Seq[Transfer]] =>
                returnMe = randomFeeTransfers match {
                    case Failure(exception) =>
                        logger.severe(s"Unable to randomize fees for transfer $input - ${exception.getMessage}")
                        Seq(Future.failed(exception))
                    case Success(value) => value.flatMap(randomTimedTransferMixer.onTransfer)
                }
            }
        }

        returnMe
    }
}
