package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.ConfigHardcoded.BOBS_ACCOUNT
import com.java1337.work.jobcoin.app.domain.Transfer
import com.java1337.work.jobcoin.app.{ConfigHardcoded, ConfigLike}
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

class AccountSplittingMixerStepTest extends AsyncFlatSpec with Matchers {

    behavior of "AccountSplittingMixerStep"

    private val DEPOSIT_ADDRESS = ConfigHardcoded.BOBS_ACCOUNT.depositAddress
    private val DEPOSIT_AMOUNT: BigDecimal = java.math.BigDecimal.TEN

    private def testOnTransferWith(input: Transfer, config: ConfigLike = new ConfigHardcoded): Seq[Future[Transfer]] = {
        val mixer = new AccountSplittingMixerStep(config)
        mixer.onTransfer(input)
    }

    it should "ignore transfers not sent to the House account" in {

        // given
        val input = Transfer(DEPOSIT_ADDRESS, "NOT_HOUSE_ACCOUNT", DEPOSIT_AMOUNT)

        // when
        val actual = testOnTransferWith(input)

        // then
        actual.size shouldBe 0
    }

    it should "split the amount among the withdrawal amounts" in {

        // given
        val config = new ConfigHardcoded
        val input = Transfer(DEPOSIT_ADDRESS, config.houseAddress, DEPOSIT_AMOUNT)

        // when
        val actual = testOnTransferWith(input, config)


        // then
        actual.size shouldBe 3

        actual.head.map { transferOne =>
            transferOne.sourceAddress shouldBe config.houseAddress
            transferOne.destinationAddress shouldBe BOBS_ACCOUNT.withdrawalPercentAndAddresses.head.address
            transferOne.amount shouldBe BOBS_ACCOUNT.withdrawalPercentAndAddresses.head.percentage * DEPOSIT_AMOUNT
        }

        actual.last.map { transferOne =>
            transferOne.sourceAddress shouldBe config.houseAddress
            transferOne.destinationAddress shouldBe BOBS_ACCOUNT.withdrawalPercentAndAddresses.last.address
            transferOne.amount shouldBe BOBS_ACCOUNT.withdrawalPercentAndAddresses.last.percentage * DEPOSIT_AMOUNT
        }
    }
}
