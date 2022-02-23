package com.java1337.work.jobcoin.app.domain

case class Transfer(
    sourceAddress: String,
    destinationAddress: String,
    amount: BigDecimal
)
