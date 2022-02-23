package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.domain.Transfer

import scala.concurrent.Future

trait MixerStepLike {
    def onTransfer(input: Transfer): Seq[Future[Transfer]]
}