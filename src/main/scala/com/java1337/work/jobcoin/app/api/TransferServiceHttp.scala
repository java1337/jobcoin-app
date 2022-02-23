package com.java1337.work.jobcoin.app.api
import com.java1337.work.jobcoin.app.{ConfigHardcoded, ConfigLike}
import com.java1337.work.jobcoin.app.domain.Transfer
import com.java1337.work.jobcoin.app.util.Closeable.autoClose
import com.java1337.work.jobcoin.app.util.{Closeable, Jackson}
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}

import java.lang.Thread.sleep
import java.time.Instant
import scala.concurrent.Future
import scala.io.Source

case class TransactionApiResponse(timestamp: Instant, fromAddress: String, toAddress: String, amount:BigDecimal)

class TransferServiceHttp(config: ConfigLike, httpClient: CloseableHttpClient) extends TransferService {

    private val sendUrl = s"${config.apiBaseUrl}/transactions"
    private val pollUrl = s"${config.apiBaseUrl}/transactions"

    private var lastTransactionTime: Instant = Instant.now

    override def send(sourceAddress: String, destinationAddress: String, amount: BigDecimal): Future[Transfer] = {
        val post = new HttpPost(getSendUrl)
        val body = s"fromAddress=$sourceAddress&toAddress=$destinationAddress&amount=$amount"
        post.setEntity(new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED))

        autoClose(httpClient.execute(post)) { response =>
            return if (response.getStatusLine.getStatusCode == HttpStatus.SC_OK) {
                Future.successful(Transfer(sourceAddress, destinationAddress, amount))
            } else {
                Future.failed(new RuntimeException(response.getStatusLine.getReasonPhrase))
            }
        }
    }

    override def poll(): Seq[Transfer] = {
        val responseBody = readTransactions
        val allTransfers = Jackson.readJsonList[TransactionApiResponse](responseBody)
        val newTransfers = allTransfers.filter { it =>
            it.fromAddress != null
        }.filter { it =>
            it.timestamp.isAfter(lastTransactionTime)
        }.map { it =>
            Transfer(it.fromAddress, it.toAddress, it.amount)
        }

        lastTransactionTime = Instant.now
        newTransfers
    }

    def getSendUrl : String = {
        sendUrl
    }

    def readTransactions: String = {
        val get = new HttpGet(pollUrl)
        autoClose(httpClient.execute(get)) { response =>
            Source.fromInputStream(response.getEntity.getContent).mkString
        }
    }
}

object TransferServiceHttp {

    def main(args: Array[String]): Unit = {
        val httpClient: CloseableHttpClient = HttpClientBuilder.create().build()
        val config = new ConfigHardcoded
        val transferService = new TransferServiceHttp(config, httpClient)
        testPoll(transferService)
        sleep(5)
        testSend(transferService)
        sleep(1)
        testPoll(transferService)
    }

    def testSend(transferService: TransferService): Unit = {
        transferService.send("Foo", "Bob", BigDecimal("10"))
//        transferService.send("Bob", "Foo", BigDecimal("60"))
    }

    def testPoll(transferService: TransferService): Unit = {
        val transfers = transferService.poll()
        println("--- poll results---")
        transfers.foreach(println)
        println
    }
}
