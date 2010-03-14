package net.fyrie.flickr
package cli

import net.liftweb.common._
import net.liftweb.common.Box._
import net.liftweb.util.IoHelpers._
import java.io.File
import scala.annotation.tailrec

object Main extends FlickrApiKey with Logger with Preferences {

  def main(args: Array[String]) {
    p("Preferences:" :: prefs.toList.map(x => "  "+x._1+" = "+x._2): _*)
    val flickr = Flickr(apiKey, apiSecret)
    (args.toList match {
      case "login" :: _ => login
      case ("authorize" | "auth") :: token => authorize(flickr, Box(token))
      case ("validate" | "valid") :: _ => validate(flickr)
      case "upload" :: _ => upload
      case unknown => Failure("Unknown command: "+unknown.mkString(" "))
    }) match {
      case f: Failure => p(f)
      case Empty => p("No Action Taken")
      case Full(s) => p(s)
      case _ => p("Done")
    }
    flickr.shutdown
  }

  def login = Full("Goto "+apiAuthUrl)

  def authorize(flickr: Flickr, token: Box[String]) =
    for {
      t <- token ?~ "token in the form of 000-000-000 required"
      r <- flickr.auth.getFullToken(t)
    } yield {
      prefs("token") = r.value
      r
    }

  def validate(flickr: Flickr) =
    for {
      t <- prefs.get("token") ?~ "need to login and authorize first"
      r <- flickr.auth.checkToken(t)
    } yield r

  def updateOrGet(key: String, value: Box[String]) =
    value.pass(_.foreach(prefs(key) = _)) or prefs.get(key)

  def upload = {
    val xmp = exec("exiftool", "-xmp", "-b", "/home/derek/Temp/test.jpg") map xml.XML.loadString

    val subjectParsers = List("""^Subject\|(.*)$""".r,"""^Treatment\|(.*)$""".r, """^(.*)$""".r)

    @tailrec
    def parseSubject(subject: String, parsers: List[util.matching.Regex]): Seq[String] = parsers match {
      case head :: tail => subject match {
        case head(found @ _*) => found.flatMap(_.split("\\|"))
        case x => parseSubject(x, tail)
      }
      case Nil => Seq.empty
    }

    xmp.map(x => (x \\ "hierarchicalSubject" \ "Bag" \ "li").flatMap(s => parseSubject(s.text, subjectParsers)))
  }

  def p(in: AnyRef*) {
    in.foreach{
      x => {
        info("> "+x)
        println(x)
      }
    }
  }

  def p(in: Failure) {
    error("> "+in.messageChain)
    println("ERROR: "+in.messageChain)
  }
}

object Flickr {
  def apply(apiKey: String, apiSecret: String): Flickr = new Flickr(apiKey, apiSecret)
}

class Flickr(val apiKey: String, val apiSecret: String) extends net.fyrie.flickr.Flickr

trait Preferences {
  val _prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass())

  object prefs extends collection.mutable.Map[String,String] {
    def +=(kv: (String, String)) = {
      _prefs.put(kv._1, kv._2)
      this
    }

    def -=(key: String) = {
      _prefs.remove(key)
      this
    }

    def get(key: String): Option[String] = Option(_prefs.get(key, null))

    def iterator: Iterator[(String, String)] =
      _prefs.keys.toList.map(k => (k, apply(k))).iterator
  }
}
