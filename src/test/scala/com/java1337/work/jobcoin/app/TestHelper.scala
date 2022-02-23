package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.domain.{Account, Transfer, WithdrawalPercentAndAddress}
import java.math.BigDecimal.ZERO

object TestHelper {

    val TWENTY_FIVE_PERCENT = BigDecimal("0.25")
    val SEVENTY_FIVE_PERCENT = BigDecimal("0.75")

    val BOBS_ACCOUNT: Account =
        Account(
            "Bobs_Deposit",
            Seq(
                WithdrawalPercentAndAddress(SEVENTY_FIVE_PERCENT, "Bobs_Rent"),
                WithdrawalPercentAndAddress(TWENTY_FIVE_PERCENT, "Bobs_Coffee")
            )
        )

    val FAKE_TRANSFER: Transfer = Transfer("Nobody", "Nowhere", ZERO)
}
