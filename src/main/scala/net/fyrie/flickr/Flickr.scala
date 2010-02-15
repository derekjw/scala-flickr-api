package net.fyrie.flickr

import xml.{Node, XML}
import dispatch._
import net.liftweb.common._
import java.security.MessageDigest


abstract class Flickr {
  val endpoint = :/("api.flickr.com") / "services" / "rest"

  val apiKey: String
  val apiSecret: String

  protected val http = new Http with Threads {
    override lazy val log: Logger = new Logger {
      val logger = org.slf4j.LoggerFactory.getLogger(classOf[Flickr])
      def info(msg: String, items: Any*) {
        logger.info(msg.format(items: _*))
      }
    }
  }

  protected def get[T](method: String, params: Map[String,Any])(block: Seq[xml.Elem] => Box[T]): Box[T] =
    http(endpoint <<? (params ++ Map("method" -> method, "api_key" -> apiKey)) <> {
      case rsp if (rsp \ "@stat").text == "ok" =>
        block(rsp.child.partialMap{case x: xml.Elem => x})
      case rsp if (rsp \ "@stat").text == "fail" =>
        Failure("Flickr API Error "+(rsp \ "err" \ "@code").text+": "+(rsp \ "err" \ "@msg").text)
      case rsp =>
        Failure("Flickr API Unknown Error: "+rsp)
    })

  object test {
    def echo(parameters: (String, String)*) =
      get("flickr.test.echo", Map(parameters:_*)){
        result =>
          Full(result.map(x => (x.label, x.text)).toMap)
      }
  }

  object auth {
    def getFrob = {
      get("flickr.auth.getFrob", Map("api_sig" -> md5SumString(apiSecret+"api_key"+apiKey+"methodflickr.auth.getFrob"))){
        result =>
          Box(result.map{
            case <frob>{frob}</frob> => frob
          }.headOption)
      }
    }
  }

  def md5SumString(bytes : String) : String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(bytes.toArray.map(_.toByte))

    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
  }

  def shutdown: Unit = http.shutdown
}

