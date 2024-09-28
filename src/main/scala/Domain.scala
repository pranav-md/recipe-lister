import java.time.Instant
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

object Domain {

  trait RecipeBase {
    val title: Option[String]
    val making_time: Option[String]
    val serves: Option[String]
    val ingredients: Option[String]
    val cost: Option[Long]
  }

  case class RecipeSubset(
                            title: Option[String],
                            making_time: Option[String],
                            serves: Option[String],
                            ingredients: Option[String],
                            cost: Option[Long]
                          ) extends RecipeBase


  case class RecipeWithId(id: Option[Long],
                          val title: Option[String] = None,
                          val making_time: Option[String] = None,
                          val serves: Option[String] = None,
                          val ingredients: Option[String] = None,
                          val cost: Option[Long] = None) extends RecipeBase


  case class RecipeFull(id: Option[Long] = None,
                        val title: Option[String] = None,
                        val making_time: Option[String] = None,
                        val serves: Option[String] = None,
                        val ingredients: Option[String] = None,
                        val cost: Option[Long] = None,
                        created_at: Instant,
                        updated_at: Instant) extends RecipeBase


  case class RequestResponse[T <: RecipeBase](message: String,
                                              recipe: List[T])

  case class DeleteResponse(message: String)

  case class RequestExceptionResponse(message: String, required: String)

  case class MissingFields(fields: List[String])

  object ResponseMessages {

    val CREATE_SUCCESS = "Recipe successfully created!"
    val CREATE_FAILURE = "Recipe creation failed!"

    val GET_BY_ID_SUCCESS = "Recipe details by id"

    val UPDATE_SUCCESS = "Recipe successfully updated!"

    val DELETE_SUCCESS = "Recipe successfully removed!"
    val DELETE_FAILURE = "No recipe found"
  }

}
