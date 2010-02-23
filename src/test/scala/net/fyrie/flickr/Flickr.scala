package net.fyrie
package flickr
package specs

import org.specs._
import net.fyrie.specs.matcher._

object Flickr extends FlickrMock with FlickrConfig

class FlickrSpec extends Specification("Flickr API") with BoxMatchers {
  import UserData._

  "flickr.test.echo" should {
    "succeed" in {
      val rsp = Flickr.test.echo("test param" -> "This is a test", "other test param" -> "another test")
      rsp must beFull.which(_ must havePairs("test_param" -> "This is a test", "other_test_param" -> "another test"))
    }
  }

  "flickr.auth.getFrob" should {
    "succeed" in {
      val rsp = Flickr.auth.getFrob
      rsp must beFull
    }
  }

  "flickr.auth.getToken" should {
    "succeed" in {
      val rsp = Flickr.auth.getToken("1234567890fakefrob")
      rsp must beFull.which(_ must be_==(Token("1234567890abcde","write",User("12345678901@N01","testuser","Test User"))))
    }
  }

  "flickr.auth.checkToken" should {
    "succeed" in {
      val rsp = Flickr.auth.checkToken("1234567890abcde")
      rsp must beFull.which(_ must be_==(Token("1234567890abcde","write",User("12345678901@N01","testuser","Test User"))))
    }
  }

  "flickr.upload" should {
    "succeed" in {
      val rsp = Flickr.upload(token.value,
                              new java.io.File("testfile.jpg"),
                              isPublic = Some(false),
                              title = Some("Test Photo"))
      rsp must beFull
    }
  }

  doAfterSpec {Flickr.shutdown}

}
