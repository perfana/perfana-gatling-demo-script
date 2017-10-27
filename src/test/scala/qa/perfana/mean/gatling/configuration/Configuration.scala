package qa.perfana.mean.gatling.configuration

import java.util.concurrent.TimeUnit
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import qa.perfana.mean.gatling.setup._
import io.gatling.commons.stats.KO


/**
 * Contains the configuration needed to build the Scenarios to run. All configuration is read from
 * the resources/application.conf file. 
 */
object Configuration {


  val isDebugActive = System.getProperty("debug") == "true"
  val useProxy = System.getProperty("useProxy") == "true"
  val graphitePrefix = System.getProperty("gatling.data.graphite.rootPathPrefix")


  System.out.println("Script settings:")
  System.out.println("useProxy: " + useProxy)
  System.out.println("graphitePrefix: " + graphitePrefix)
  System.out.println("debug: " + isDebugActive)

  //  get targetBaseUrl
  val targetBaseUrl = System.getProperty("targetBaseUrl")
  System.out.println("targetBaseUrl: " + targetBaseUrl)

  // get load figures
  val initialUsersPerSecond  = System.getProperty("initialUsersPerSecond").toDouble
  val targetUsersPerSecond  = System.getProperty("targetUsersPerSecond").toDouble
  val rampUpPeriodInSeconds = ( System.getProperty("rampUpPeriodInSeconds").toLong, TimeUnit.SECONDS )
  val constantUsagePeriodInSeconds = ( System.getProperty("constantUsagePeriodInSeconds").toLong, TimeUnit.SECONDS )

  System.out.println("initialUsersPerSecond: " + initialUsersPerSecond)
  System.out.println("targetUsersPerSecond: " + targetUsersPerSecond)
  System.out.println("rampUpPeriodInSeconds: " + rampUpPeriodInSeconds)
  System.out.println("constantUsagePeriodInSeconds: " + constantUsagePeriodInSeconds)


  /**
    * This determines what scenario to use as baseScenario based on the profile
    * selected when starting the test
    */

  val scenario = System.getProperty("scenario")

  val baseScenario = scenario match {

    case "debug" => Scenarios.debugScenario
    case "acceptance"  => Scenarios.acceptanceTestScenario

    case _ => Scenarios.acceptanceTestScenario


  }

  private val baseHttpProtocol = http
    .baseURL(targetBaseUrl) // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    //    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    .extraInfoExtractor(ExtraInfo => {
      if(ExtraInfo.status == KO)
        println("httpCode: " + ExtraInfo.response.statusCode + ", body: "+ ExtraInfo.response.body)
      Nil
    })

  private val baseHttpDebugProtocol = http
    .baseURL(targetBaseUrl) // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  def httpDebugProtocol ={
    /* Add proxy if specified */
    if(useProxy) {
      System.out.println("Using proxy at localhost port 8888!")
      baseHttpDebugProtocol.proxy(Proxy("localhost", 8888).httpsPort(8888))
    }else{
      baseHttpDebugProtocol
    }
  }

  def httpProtocol ={
    (baseHttpDebugProtocol)
  }



}
