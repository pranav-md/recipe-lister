import Domain.{DeleteResponse, RecipeFull, RecipeBase, RecipeWithId, RecipeSubset, RequestExceptionResponse, RequestResponse}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object Implicits {

  object RecipeSubsetImplicits {
    implicit val recipeRequestDecoder: Decoder[RecipeSubset] = deriveDecoder[RecipeSubset]
    implicit val recipeRequestEncoder: Encoder.AsObject[RecipeSubset] = deriveEncoder[RecipeSubset]
  }

  object RecipeWithIdImplicits {
    implicit val recipeResponseDecoder: Decoder[RecipeWithId] = deriveDecoder[RecipeWithId]
    implicit val recipeResponseEncoder: Encoder.AsObject[RecipeWithId] = deriveEncoder[RecipeWithId]
  }

  object RecipeFullImplicits {
    implicit val recipeEncoder: Encoder[RecipeFull] = deriveEncoder[RecipeFull]
    implicit val recipeDecoder: Decoder[RecipeFull] = deriveDecoder[RecipeFull]
  }

  object DeleteResponseImplicits {
    implicit val deleteResponseEncoder: Encoder[DeleteResponse] = deriveEncoder[DeleteResponse]
    implicit val deleteResponseDecoder: Decoder[DeleteResponse] = deriveDecoder[DeleteResponse]
  }

  object RequestResponseImplicits {

    // Encoder and Decoder for RequestResponse
    implicit def requestResponseEncoder[T <: RecipeBase : Encoder]: Encoder[RequestResponse[T]] =
      deriveEncoder[RequestResponse[T]]

    implicit def requestResponseDecoder[T <: RecipeBase : Decoder]: Decoder[RequestResponse[T]] =
      deriveDecoder[RequestResponse[T]]
  }

  object RequestExceptionResponseImplicits {
    implicit val requestExceptionResponseEncoder: Encoder[RequestExceptionResponse] = deriveEncoder[RequestExceptionResponse]
    implicit val requestExceptionResponseDecoder: Decoder[RequestExceptionResponse] = deriveDecoder[RequestExceptionResponse]
  }
}
