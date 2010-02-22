package net.fyrie
package flickr
package specs

import org.specs._
import net.fyrie.specs.matcher._

class Flickr extends flickr.Flickr with FlickrConfig

class FlickrSpec extends Specification("Flickr API") with BoxMatchers {

  val flickr = new Flickr

  "flickr.test.echo" should {
    "succeed" in {
      val rsp = flickr.test.echo("test param" -> "This is a test", "other test param" -> "another test")
      rsp must beFull.which(_ must havePairs("test_param" -> "This is a test", "other_test_param" -> "another test"))
    }
  }

  "flickr.auth.getFrob" should {
    "succeed" in {
      val rsp = flickr.auth.getFrob
      rsp must beFull
    }
  }

  doAfterSpec {flickr.shutdown}

}
