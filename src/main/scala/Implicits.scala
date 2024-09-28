import Domain.{DeleteResponse, RecipeFull, RecipeBase, RecipeWithId, RecipeSubset, RequestExceptionResponse, RequestResponse}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object Implicits {

  object RecipeSubsetImplicits {
    implicit val recipeRequestDecoder: Decoder[RecipeSubset] = deriveDecoder[RecipeSubset]
    implicit val recipeRequestEncoder: Encoder.AsObject[RecipeSubset] = deriveEncoder[RecipeSubset]
  }

  object RecipeWithIdImplicits {
    implicit val recipeResponseEncoder: Encoder.AsObject[RecipeWithId] = deriveEncoder[RecipeWithId]
  }

  object RecipeFullImplicits {
    implicit val recipeEncoder: Encoder[RecipeFull] = deriveEncoder[RecipeFull]
  }

  object DeleteResponseImplicits {
    implicit val deleteResponseEncoder: Encoder[DeleteResponse] = deriveEncoder[DeleteResponse]
  }

  object RequestResponseImplicits {

    // Encoder and Decoder for RequestResponse
    implicit def requestResponseEncoder[T <: RecipeBase : Encoder]: Encoder[RequestResponse[T]] =
      deriveEncoder[RequestResponse[T]]
  }

  object RequestExceptionResponseImplicits {
    implicit val requestExceptionResponseEncoder: Encoder[RequestExceptionResponse] = deriveEncoder[RequestExceptionResponse]
  }
}
