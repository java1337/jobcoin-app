package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.domain.Account

trait ConfigLike {

    def accounts: Seq[Account]
    def apiBaseUrl : String
    def houseAddress: String
    def minimumFeePercentage: Int
    def maximumFeePercentage: Int
}
