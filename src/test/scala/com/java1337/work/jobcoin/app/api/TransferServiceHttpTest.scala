package com.java1337.work.jobcoin.app.api

import com.java1337.work.jobcoin.app.ConfigHardcoded
import com.java1337.work.jobcoin.app.domain.Transfer
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.scalatest.{AsyncFlatSpec, Matchers}

class TransferServiceHttpTest extends AsyncFlatSpec with Matchers {

    behavior of "TransferServiceHttp.send"

    it should "send transfer to httpbin" in {
        // given
        val httpClient: CloseableHttpClient = HttpClientBuilder.create().build()
        val config = new ConfigHardcoded
        val transferService = new TransferServiceHttp(config, httpClient) {
            override def getSendUrl = "https://httpbin.org/post"
        }

        // when
        val actual = transferService.send("source", "destination", BigDecimal("13.37"))

        // then
        actual map { it: Transfer =>
            it.amount shouldBe BigDecimal("13.37")
            it.sourceAddress shouldBe "source"
            it.destinationAddress shouldBe "destination"
        }
    }
}
