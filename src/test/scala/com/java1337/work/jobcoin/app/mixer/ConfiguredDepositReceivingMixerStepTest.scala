package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.ConfigHardcoded.BOBS_ACCOUNT
import com.java1337.work.jobcoin.app.domain.Transfer
import com.java1337.work.jobcoin.app.{ConfigHardcoded, TransferServiceFake}
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

class ConfiguredDepositReceivingMixerStepTest extends AsyncFlatSpec with Matchers {

    behavior of "ConfiguredDepositReceivingMixerStep"

    it should "transfer amount to house address when the destination address is a configured deposit address" in {

        // given
        val accounts = Seq(BOBS_ACCOUNT)
        val config = new ConfigHardcoded()
        val expected = Transfer(BOBS_ACCOUNT.depositAddress, config.houseAddress, 10)
        val fakeTransferService = new TransferServiceFake(Future.successful(expected))

        val transfer = Transfer("Bob", BOBS_ACCOUNT.depositAddress, 10)
        val mixer = new ConfiguredDepositReceivingMixerStep(config, fakeTransferService)

        // when
        val actual = mixer.onTransfer(transfer)

        // then
        actual.size shouldBe 1
        actual.head.map { actualTransfer =>
            actualTransfer shouldBe expected
        }

        fakeTransferService.getMaybeLastSentTransfer shouldBe Some(expected)
    }

    it should "ignore transfers when the destination address is not a configured deposit address" in {

        // given
        val accounts = Seq(BOBS_ACCOUNT)
        val config = new ConfigHardcoded()
        val expected = Transfer(BOBS_ACCOUNT.depositAddress, config.houseAddress, 10)
        val fakeTransferService = new TransferServiceFake(Future.successful(expected))

        val transfer = Transfer("Bob", "Not_" + BOBS_ACCOUNT.depositAddress, 10)
        val mixer = new ConfiguredDepositReceivingMixerStep(config, fakeTransferService)

        // when
        val actual = mixer.onTransfer(transfer)

        // then
        actual.size shouldBe 0
        fakeTransferService.getMaybeLastSentTransfer shouldBe None
    }
}
