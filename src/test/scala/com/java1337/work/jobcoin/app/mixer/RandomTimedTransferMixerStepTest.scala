package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.{ConfigHardcoded, TransferServiceFake}
import com.java1337.work.jobcoin.app.TestHelper.BOBS_ACCOUNT
import com.java1337.work.jobcoin.app.domain.Transfer
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

class RandomTimedTransferMixerStepTest extends AsyncFlatSpec with Matchers {

    behavior of "RandomTimedTransferMixerStep"

    it should "delay sending the transfer by some amount" in {

        // given
        val config = new ConfigHardcoded()
        val transfer = Transfer("Bob", BOBS_ACCOUNT.depositAddress, 10)
        val fakeTransferService = new TransferServiceFake(Future.successful(transfer))

        val mixer = new RandomTimedTransferMixerStep(fakeTransferService, 3, 7)

        // when
        val startTimeMillis = System.currentTimeMillis()
        val actual = mixer.onTransfer(transfer)

        // then
        actual.size shouldBe 1
        actual.head map { actualTransfer =>
            val finishTime = System.currentTimeMillis()
            val delayMillis = finishTime - startTimeMillis
            actualTransfer.sourceAddress shouldBe transfer.sourceAddress
            actualTransfer.destinationAddress shouldBe transfer.destinationAddress
            actualTransfer.amount shouldBe transfer.amount
            assert (delayMillis > 2000)
            assert (delayMillis < 8000)
        }
    }
}
