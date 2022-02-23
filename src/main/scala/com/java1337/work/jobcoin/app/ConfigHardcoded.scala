package com.java1337.work.jobcoin.app
import com.java1337.work.jobcoin.app.domain.Account

class ConfigHardcoded(
    override val minimumFeePercentage: Int = 1,
    override val maximumFeePercentage: Int = 9
) extends ConfigLike {

    override def accounts: Seq[Account] = Seq()
    override val apiBaseUrl: String = "https://jobcoin.gemini.com/lifting-huff/api"
    override def houseAddress: String = "JobCoinHouse"

}
