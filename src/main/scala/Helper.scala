import Domain.{MissingFields, RecipeBase}
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import io.circe.{Decoder, Encoder, Json, JsonObject}
import io.circe.jawn.decode
import io.circe.syntax.EncoderOps

import scala.concurrent.Future

object Helper {
  def validateRecipe(recipe: RecipeBase): MissingFields = {
    // Collect missing fields
    val missingFields = List(
      recipe.title.fold("title")(t => ""),
      recipe.making_time.fold("making_time")(t => ""),
      recipe.serves.fold("serves")(s => ""),
      recipe.ingredients.fold("ingredients")(i => ""),
      recipe.cost.fold("cost")(c => "")
    ).filter(_.nonEmpty)

    MissingFields(missingFields)
  }

  def renameField(json: Json, oldField: String, newField: String): Json = {
    json.asObject.map { jsonObj =>
      // Convert JsonObject to Map and rename the field
      val updatedJsonObj = JsonObject.fromMap(
        jsonObj.toMap.map {
          case (`oldField`, value) => newField -> value // Rename the field
          case other => other // Keep the rest unchanged
        }
      )
      Json.fromJsonObject(updatedJsonObj)
    }
  }.getOrElse(Json.obj())

  def completeResponse(resp: Json): StandardRoute =
    complete(HttpEntity(ContentTypes.`application/json`, resp.noSpaces))

  object MarshallerImplicits {


    object AkkaCirceSupport {

      implicit final def unmarshaller[E: Decoder]: FromEntityUnmarshaller[E] = {
        Unmarshaller.stringUnmarshaller
          .flatMap { context =>
            materialiser =>
              json =>
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
}
