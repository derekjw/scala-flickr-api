package net.fyrie.flickr

import xml.{Node, XML}
import dispatch._
import net.liftweb.common._
import java.security.MessageDigest
import collection.SortedMap

abstract class Flickr {
  val endpoint = :/("api.flickr.com") / "services" / "rest"

  val apiKey: String
  val apiSecret: String

  val params = new {
    def apply(method: String) = SortedMap("api_key" -> apiKey, "method" -> method)
  }

  val http = new Http with Threads {
    override lazy val log: Logger = new Logger {
      val logger = org.slf4j.LoggerFactory.getLogger(classOf[Flickr])
      def info(msg: String, items: Any*) {
        logger.info(msg.format(items: _*))
      }
    }
  }

  def get[T](query: SortedMap[String,Any])(block: Seq[xml.Elem] => Box[T]): Box[T] =
    http(endpoint <<? query <> {
      case rsp if (rsp \ "@stat").text == "ok" =>
        block(rsp.child.partialMap{case x: xml.Elem => x})
      case rsp if (rsp \ "@stat").text == "fail" =>
        Failure("Flickr API Error "+(rsp \ "err" \ "@code").text+": "+(rsp \ "err" \ "@msg").text)
      case rsp =>
        Failure("Flickr API Unknown Error: "+rsp)
    })

  def sign(query: SortedMap[String,Any]) =
    query + ("api_sig" -> md5SumString(apiSecret+query.map{case (k,v) => k+v}.mkString))

  object test {
    def echo(values: (String, String)*) =
      get(params("flickr.test.echo") ++ values){
        result =>
          Full(result.map(x => (x.label, x.text)).toMap)
      }
  }

  object auth {
    def getFrob =
      get(sign(params("flickr.auth.getFrob"))){
        result =>
          Box(result.map{
            case <frob>{frob}</frob> => frob.toString
          }.headOption)
      }

    def loginUrl(frob: String) =
      (:/("api.flickr.com") / "services" / "auth" <<? sign(SortedMap("api_key" -> apiKey, "frob" -> frob, "perms" -> "read"))).to_uri
  }

  def md5SumString(bytes : String) : String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(bytes.toArray.map(_.toByte))

    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
  }

  def shutdown: Unit = http.shutdown
}

