package com.java1337.work.jobcoin.app

import com.java1337.work.jobcoin.app.api.TransferService
import com.java1337.work.jobcoin.app.domain.Transfer

import scala.concurrent.Future

class TransferServiceFake(sendResponse: Future[Transfer]) extends TransferService {

    private var maybeLastSentTransfer : Option[Transfer] = None

    override def send(sourceAddress: String, destinationAddress: String, amount: BigDecimal): Future[Transfer] = {
        maybeLastSentTransfer = Some(Transfer(sourceAddress, destinationAddress, amount))
        sendResponse
    }

    def getMaybeLastSentTransfer: Option[Transfer] = maybeLastSentTransfer

    override def poll(): Seq[Transfer] = ???
}
