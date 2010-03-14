package net.fyrie
package flickr

import net.liftweb.common._
import collection.SortedMap

abstract class FlickrMock extends Flickr {

  override def get[T](query: SortedMap[String,Any])(block: Seq[xml.Elem] => Box[T]): Box[T] =
    Box(query.get("method")).flatMap{
      case "flickr.test.echo" =>
        block(query.toList.map{case (k,v) =>
          xml.Elem(null, k.replaceAll(" ", "_"), xml.Null, xml.TopScope, xml.Text(v.toString))
        })
      case "flickr.auth.getFrob" =>
        block(List(<frob>12345678901234567-abcdefabcdefabcd-123456</frob>))
      case "flickr.auth.getToken" =>
        block(List(<auth>
                     <token>1234567890abcde</token>
                     <perms>write</perms>
                     <user nsid="12345678901@N01" username="testuser" fullname="Test User" />
                   </auth>))
      case "flickr.auth.checkToken" =>
        block(List(<auth>
                     <token>1234567890abcde</token>
                     <perms>write</perms>
                     <user nsid="12345678901@N01" username="testuser" fullname="Test User" />
                   </auth>))
      case _ => super.get(query)(block)
    }

  override def multipartUpload[T](files: Seq[(String,java.io.File)], query: SortedMap[String,Any])(block: Seq[xml.Elem] => Box[T]): Box[T] =
    block(List(<photoid>1234567890</photoid>))

}

