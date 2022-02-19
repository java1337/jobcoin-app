package com.java1337.work.jobcoin.app

import org.scalatest.{FlatSpec, Matchers}

class ConfigHardcodedTest extends FlatSpec with Matchers {

  behavior of "ConfigHardcoded"

  it should "have the right apiBaseUrl" in {
    // given
    val config = new ConfigHardcoded()

    // when / then
    withClue("the API base url is hardcoded to the right value") {
      config.apiBaseUrl shouldBe "https://jobcoin.gemini.com/lifting-huff/api"
    }
  }
}
