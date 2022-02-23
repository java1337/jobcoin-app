package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.ConfigHardcoded.{BOBS_ACCOUNT, MAX_FEE_PERCENT_DEFAULT, MAX_WITHDRAWAL_DELAY, MIN_FEE_PERCENT_DEFAULT, MIN_WITHDRAWAL_DELAY, TRANSACTION_POLLING_INTERVAL_SECONDS}
import com.java1337.work.jobcoin.app.domain.{Account, WithdrawalPercentAndAddress}

class ConfigHardcoded(
    override val minimumFeePercentage: Int = MIN_FEE_PERCENT_DEFAULT,
    override val maximumFeePercentage: Int = MAX_FEE_PERCENT_DEFAULT
) extends ConfigLike {

    override val accounts: Seq[Account] = Seq(BOBS_ACCOUNT)
    override val apiBaseUrl: String = "https://jobcoin.gemini.com/lifting-huff/api"
    override val houseAddress: String = "JobCoinHouse"
    override val transactionPollingIntervalInSeconds: Int = TRANSACTION_POLLING_INTERVAL_SECONDS
    override val minimumWithdrawalTransferDelayInSeconds: Int = MIN_WITHDRAWAL_DELAY
    override val maximumWithdrawalTransferDelayInSeconds: Int = MAX_WITHDRAWAL_DELAY

}

object ConfigHardcoded {
    val MIN_FEE_PERCENT_DEFAULT: Int = 1
    val MAX_FEE_PERCENT_DEFAULT: Int = 9

    val MIN_WITHDRAWAL_DELAY: Int = 0
    val MAX_WITHDRAWAL_DELAY: Int = 30

    val TRANSACTION_POLLING_INTERVAL_SECONDS: Int = 5

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
}
