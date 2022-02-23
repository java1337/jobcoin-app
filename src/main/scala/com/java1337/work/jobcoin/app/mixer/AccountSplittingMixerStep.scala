package com.java1337.work.jobcoin.app.mixer
import com.java1337.work.jobcoin.app.ConfigLike
import com.java1337.work.jobcoin.app.domain.{Transfer, WithdrawalPercentAndAddress}

import java.util.logging.Logger
import scala.concurrent.Future

class AccountSplittingMixerStep(config: ConfigLike) extends MixerStepLike {

    private val logger: Logger = Logger.getLogger(getClass.getName)

    override def onTransfer(input: Transfer): Seq[Future[Transfer]] = {
        if (!config.houseAddress.equals(input.destinationAddress)) {
            logger.info(s"Ignoring transfer from ${input.sourceAddress} to ${input.destinationAddress} of ${input.amount} Jobcoin")
            return Seq()
        }
        config.supportedDepositAccountMap.get(input.sourceAddress) match {
            case Some(account) =>
                logger.info(s"Splitting transfer from ${input.sourceAddress} via ${input.destinationAddress} of ${input.amount} Jobcoin to...")
                account.withdrawalPercentAndAddresses.map { withdrawalPercentAndAccount: WithdrawalPercentAndAddress =>
                    val splitAmount = withdrawalPercentAndAccount.percentage * input.amount
                    logger.info(s"    * ${withdrawalPercentAndAccount.address} -> $splitAmount Jobcoin (${100.0 * withdrawalPercentAndAccount.percentage}%)")
                    Future.successful(
                        Transfer(
                            input.destinationAddress,
                            withdrawalPercentAndAccount.address,
                            withdrawalPercentAndAccount.percentage * input.amount))
                }
            case None => Seq()
        }
    }
}
