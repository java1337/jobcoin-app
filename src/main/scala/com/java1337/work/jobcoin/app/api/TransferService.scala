package com.java1337.work.jobcoin.app.api

import com.java1337.work.jobcoin.app.domain.Transfer

import scala.concurrent.Future

trait TransferService {

    def send(sourceAddress: String, destinationAddress: String, amount: BigDecimal): Future[Transfer]
    def poll(): Seq[Transfer]
}
