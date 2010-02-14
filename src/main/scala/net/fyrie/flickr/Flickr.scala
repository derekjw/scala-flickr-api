package net.fyrie.flickr

import xml.{Node, XML}
import dispatch._
import net.liftweb.common._

class Flickr(apiKey: String) {
  val endpoint = :/("api.flickr.com") / "services" / "rest"

  protected val http = new Http with Threads {
    override lazy val log: Logger = new Logger {
      val logger = org.slf4j.LoggerFactory.getLogger(classOf[Flickr])
      def info(msg: String, items: Any*) {
        logger.info(msg.format(items: _*))
      }
    }
  }

  protected def get[T](method: String, params: Map[String,Any])(block: Seq[xml.Elem] => T): Box[T] =
    http(endpoint <<? (params ++ Map("method" -> method, "api_key" -> apiKey)) <> {
      case rsp if (rsp \ "@stat").text == "ok" => Full(block(rsp.child.partialMap{case x: xml.Elem => x}))
      case rsp if (rsp \ "@stat").text == "fail" => Failure("Flickr API Error "+(rsp \ "err" \ "@code").text+": "+(rsp \ "err" \ "@msg").text)
      case rsp => Failure("Flickr API Unknown Error: "+rsp)
    })

  object test {
    def echo(parameters: (String, String)*) =
      get("flickr.test.echo", Map(parameters:_*)){
        _.map(x => (x.label, x.text)).toMap
      }
  }

  def shutdown: Unit = http.shutdown
}
