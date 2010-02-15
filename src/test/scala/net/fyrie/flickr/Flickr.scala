package net.fyrie.flickr
package specs

import org.specs._

class FlickrSpec extends Specification("Flickr API") {

  val flickr = new Flickr with FlickrConfig

  "api requests" should {
    "succeed with echo test" in {
      val response = flickr.test.echo("test param" -> "This is a test", "other test param" -> "another test")
      response.toOption must beSome.which(_ must havePairs("test_param" -> "This is a test", "other_test_param" -> "another test"))
    }
  }

  doAfterSpec {flickr.shutdown}
}
