package net.fyrie.flickr
package specs

import org.specs._

import net.lag.configgy.Configgy

class FlickrSpec extends Specification("Flickr API") {

  Configgy.configure("flickr.conf")
  
  val config = Configgy.config

  val flickr = new Flickr(config.getString("key", "APIKEY"))

  "api requests" should {
    "succeed with echo test" in {
      val response = flickr.test.echo("test param" -> "This is a test", "other test param" -> "another test")
      response.toOption must beSome.which(_ must havePairs("test_param" -> "This is a test", "other_test_param" -> "another test"))
    }
  }

  doAfterSpec {flickr.shutdown}
}
