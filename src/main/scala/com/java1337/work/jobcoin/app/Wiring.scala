package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.api.TransferServiceHttp
import com.java1337.work.jobcoin.app.domain.{Account, WithdrawalPercentAndAddress}
import com.java1337.work.jobcoin.app.mixer.{AccountSplittingMixerStep, ConfiguredDepositReceivingMixerStep, DistributingMixer, RandomFeeMixerStep, RandomTimedTransferMixerStep}
import org.apache.http.impl.client.HttpClientBuilder

object Wiring {

    val FIFTY_PERCENT = BigDecimal("0.50")
    val THIRTY_PERCENT = BigDecimal("0.30")
    val TWENTY_PERCENT = BigDecimal("0.20")

    val BOBS_ACCOUNT: Account =
        Account(
            "Bobs_Deposit",
            Seq(
                WithdrawalPercentAndAddress(FIFTY_PERCENT, "Bobs_Rent"),
                WithdrawalPercentAndAddress(THIRTY_PERCENT, "Bobs_Groceries"),
                WithdrawalPercentAndAddress(TWENTY_PERCENT, "Bobs_Coffee")
            )
        )

    val config = new ConfigHardcoded

    val transferService = new TransferServiceHttp(config, HttpClientBuilder.create().build())

    val accountSplittingMixerStep = new AccountSplittingMixerStep(config, Seq(BOBS_ACCOUNT), transferService)
    val randomFeeMixerStep = new RandomFeeMixerStep(config)
    val randomTimedTransferMixerStep = new RandomTimedTransferMixerStep(transferService)

    val depositReceivingMixerStep = new ConfiguredDepositReceivingMixerStep(config, Seq(BOBS_ACCOUNT), transferService)
    val distributingMixer = new DistributingMixer(accountSplittingMixerStep, randomFeeMixerStep, randomTimedTransferMixerStep)

    val pollingWorkThread = new PollingWorkThread(transferService, depositReceivingMixerStep, distributingMixer)
}
