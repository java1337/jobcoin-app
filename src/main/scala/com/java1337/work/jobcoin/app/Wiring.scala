package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.api.TransferServiceHttp
import com.java1337.work.jobcoin.app.mixer._
import org.apache.http.impl.client.HttpClientBuilder

object Wiring {

    val config = new ConfigHardcoded

    val transferService = new TransferServiceHttp(config, HttpClientBuilder.create().build())

    val accountSplittingMixerStep = new AccountSplittingMixerStep(config)
    val randomFeeMixerStep = new RandomFeeMixerStep(config)
    val randomTimedTransferMixerStep = new RandomTimedTransferMixerStep(config, transferService)

    val depositReceivingMixerStep = new ConfiguredDepositReceivingMixerStep(config, transferService)
    val distributingMixer = new DistributingMixer(accountSplittingMixerStep, randomFeeMixerStep, randomTimedTransferMixerStep)

    val pollingWorkThread = new PollingWorkThread(config, transferService, depositReceivingMixerStep, distributingMixer)
}
