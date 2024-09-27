import akka.http.scaladsl.common.CsvEntityStreamingSupport
import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, HttpEntity}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import io.circe.jawn.decode
import io.circe.{Decoder, Encoder}
import akka.http.scaladsl.marshalling.{Marshaller, Marshalling, ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import io.circe.syntax.EncoderOps

import scala.concurrent.Future

object MarshallerImplicits {


  object AkkaCirceSupport {

    implicit final def unmarshaller[E: Decoder]: FromEntityUnmarshaller[E] = {
      Unmarshaller.stringUnmarshaller
        .flatMap { context => materialiser => json =>
          decode[E](json).fold(Future.failed, Future.successful)
        }
    }

    implicit final def marshaller[E: Encoder]: ToResponseMarshaller[E] = {
      Marshaller.withFixedContentType(`application/json`) { entity =>
        import akka.http.scaladsl.model.HttpResponse
        HttpResponse.apply(entity = HttpEntity(`application/json`, entity.asJson.noSpaces))
      }
    }

    implicit def toResponseMarshallable[E: Encoder](input: E): ToResponseMarshallable = ToResponseMarshallable(input)

  }

}
