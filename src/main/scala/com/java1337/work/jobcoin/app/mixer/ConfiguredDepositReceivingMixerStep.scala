package com.java1337.work.jobcoin.app.mixer
import com.java1337.work.jobcoin.app.ConfigLike
import com.java1337.work.jobcoin.app.api.TransferService
import com.java1337.work.jobcoin.app.domain.{Account, Transfer}

import java.util.logging.Logger
import scala.concurrent.Future

/**
 * 4. The mixer will detect your transfer by watching or polling the P2P Bitcoin network
 * 5. The mixer will transfer your bitcoin from the deposit address into a big “house
 *    account” along with all the other bitcoin currently being mixed
 */
class ConfiguredDepositReceivingMixerStep(config: ConfigLike, transferService: TransferService) extends MixerStepLike {

    private val logger: Logger = Logger.getLogger(getClass.getName)

    override def onTransfer(input: Transfer): Seq[Future[Transfer]] = {
        val maybeAccount: Option[Account] = config.supportedDepositAccountMap.get(input.destinationAddress)
        maybeAccount match {
            case Some(_) =>
                logger.info(s"Processing transfer from account ${input.sourceAddress} to ${input.destinationAddress} of ${input.amount} Jobcoin")
                Seq(transferService.send(input.destinationAddress, config.houseAddress, input.amount))
            case None =>
                logger.info(s"Ignoring transfer from account ${input.sourceAddress} to ${input.destinationAddress} of ${input.amount} Jobcoin because destination account is not a configured deposit account")
                Seq()
        }
    }
}
