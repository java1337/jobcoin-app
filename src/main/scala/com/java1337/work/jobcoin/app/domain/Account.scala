package com.java1337.work.jobcoin.app.domain

case class Account(
    // 2. The mixer provides you with a new ‘deposit’ address that it owns
    depositAddress: String,
    // 1. You provide a list of new, unused ‘withdrawal’ addresses that you own to the mixer
    withdrawalPercentAndAddresses: Seq[WithdrawalPercentAndAddress]
)
