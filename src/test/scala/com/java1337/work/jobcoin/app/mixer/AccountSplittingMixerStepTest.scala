package com.java1337.work.jobcoin.app.mixer

import com.java1337.work.jobcoin.app.{ConfigHardcoded, ConfigLike, TransferServiceFake}
import com.java1337.work.jobcoin.app.TestHelper.{BOBS_ACCOUNT, FAKE_TRANSFER}
import com.java1337.work.jobcoin.app.domain.Transfer
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

class AccountSplittingMixerStepTest extends AsyncFlatSpec with Matchers {

    behavior of "AccountSplittingMixerStep"

    val DEPOSIT_AMOUNT: BigDecimal = java.math.BigDecimal.TEN

    private def testOnTransferWith(input: Transfer, config: ConfigLike = new ConfigHardcoded): Seq[Future[Transfer]] = {
        val accounts = Seq(BOBS_ACCOUNT)
        val fakeTransferService = new TransferServiceFake(Future.successful(FAKE_TRANSFER))
        val mixer = new AccountSplittingMixerStep(config, accounts, fakeTransferService)

        mixer.onTransfer(input)
    }

    it should "ignore transfers not sent to the House account" in {

        // given
        val input = Transfer(BOBS_ACCOUNT.depositAddress, "NOT_HOUSE_ACCOUNT", DEPOSIT_AMOUNT)

        // when
        val actual = testOnTransferWith(input)

        // then
        actual.size shouldBe 0
    }

    it should "split the amount among the withdrawal amounts" in {

        // given
        val config = new ConfigHardcoded
        val input = Transfer(BOBS_ACCOUNT.depositAddress, config.houseAddress, DEPOSIT_AMOUNT)

        // when
        val actual = testOnTransferWith(input, config)


        // then
        actual.size shouldBe 2

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
