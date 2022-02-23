package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.ConfigHardcoded
import com.java1337.work.jobcoin.app.TestHelper.BOBS_ACCOUNT
import com.java1337.work.jobcoin.app.domain.Transfer
import org.scalatest.{AsyncFlatSpec, Matchers}


class RandomFeeMixerStepTest extends AsyncFlatSpec with Matchers {

    behavior of "RandomFeeMixerStep"

    it should "reduce the amount by a random percentage" in {

        // given
        val config = new ConfigHardcoded()
        val transfer = Transfer("Bob", BOBS_ACCOUNT.depositAddress, 10)
        val mixer = new RandomFeeMixerStep(config)

        // when
        val actual = mixer.onTransfer(transfer)

        // then
        actual.size shouldBe 1
        actual.head map { actualTransfer =>
            actualTransfer.sourceAddress shouldBe transfer.sourceAddress
            actualTransfer.destinationAddress shouldBe transfer.destinationAddress
            val percentageFee = 100.0 * (transfer.amount - actualTransfer.amount) / transfer.amount
            assert(percentageFee >= config.minimumFeePercentage)
            assert(percentageFee <= config.maximumFeePercentage)
        }
    }

    it should "reduce the amount by an exact percentage if min/max fee are the same" in {

        // given
        val config = new ConfigHardcoded(25, 25)
        val transfer = Transfer("Bob", BOBS_ACCOUNT.depositAddress, 10)
        val mixer = new RandomFeeMixerStep(config)

        // when
        val actual = mixer.onTransfer(transfer)

        // then
        actual.size shouldBe 1
        actual.head map { actualTransfer =>
            actualTransfer.sourceAddress shouldBe transfer.sourceAddress
            actualTransfer.destinationAddress shouldBe transfer.destinationAddress
            val percentageFee = 100.0 * (transfer.amount - actualTransfer.amount) / transfer.amount
            assert(percentageFee == config.minimumFeePercentage)
            assert(percentageFee == config.maximumFeePercentage)
            assert(actualTransfer.amount === 7.5)
        }
    }
}
