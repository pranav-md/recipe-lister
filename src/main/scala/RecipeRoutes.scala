import Domain.{DeleteResponse, RecipeFull, RecipeSubset, RecipeWithId, RequestExceptionResponse, RequestResponse, ResponseMessages}
import Domain.MissingFields
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, onComplete, path, pathPrefix}
import akka.http.scaladsl.server.{PathMatchers, Route}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.Directives._
import Implicits.RecipeSubsetImplicits._
import Implicits.RecipeWithIdImplicits._
import Implicits.DeleteResponseImplicits._
import Implicits.RecipeFullImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import Helper.MarshallerImplicits._
import io.circe.syntax._
import Implicits.RequestResponseImplicits._
import Implicits.RequestExceptionResponseImplicits._
import Helper.{completeResponse, renameField, validateRecipe}
import com.typesafe.scalalogging.LazyLogging

object RecipeRoutes extends LazyLogging {

  val RECIPE_ROUTE = "recipes"

  val routes: Route = DebuggingDirectives.logRequest("recipes") {
    pathPrefix(RECIPE_ROUTE) {
      createRecipeRequest ~ getRecipeById ~ getRecipeRequest ~ updateRecipeById ~ deleteRecipeById
    }
  }

  val createRecipeRequest = post {
    entity(as[RecipeSubset]) { body =>
      val recipeCreation = validateRecipe(body) match {
        case missingFields: MissingFields if missingFields.fields.isEmpty =>
          val insertedRecipeFuture = RecipeService.createRecipe(body)
          insertedRecipeFuture.transform {
            case Success(insertedRecipe) =>
              val res = RequestResponse[RecipeFull](ResponseMessages.CREATE_SUCCESS, List(insertedRecipe))
              Success(res.asJson)
            case Failure(exception) => Failure(exception)
          }
        case missingFields: MissingFields =>
          logger.info(s"Found fields: ${missingFields.fields.mkString(", ")} missing.")
          val res = RequestExceptionResponse(ResponseMessages.CREATE_FAILURE, missingFields.fields.mkString("", ", ", ""))
          Future(res.asJson)
      }

      onComplete(recipeCreation) {
        case Success(response) =>
          completeResponse(response.asJson)
      }
    }
  }

  val getRecipeRequest = get {
    val recipes = RecipeService.getAllRecipes()
    onComplete(recipes.map(RequestResponse[RecipeWithId]("", _))) {
      case Success(resp) =>
        val response = renameField(resp.asJson, "recipe", "recipes")
          .mapObject(_.remove("message"))
        completeResponse(response.asJson)
    }
  }

  val getRecipeById = path(PathMatchers.LongNumber) { recipeId =>
    get {
      onComplete {
        val res = RecipeService.getRecipeById(recipeId)
        res.map(recipe => RequestResponse[RecipeWithId](
          ResponseMessages.GET_BY_ID_SUCCESS,
          List(recipe)))
      } {
        case Success(resp) =>
          complete(HttpEntity(ContentTypes.`application/json`, resp.asJson.toString))
        case Failure(_) =>
          complete(StatusCodes.NotFound -> s"Recipe with id $recipeId not found.")
      }
    }
  }

  val updateRecipeById = path(PathMatchers.LongNumber) { recipeId =>
    patch {
      entity(as[RecipeSubset]) { body =>
        onComplete (RecipeService.updateRecipeById(recipeId, body)) {
          case Success(updatedRecipe) =>
            val response = RequestResponse[RecipeSubset](
              ResponseMessages.UPDATE_SUCCESS,
              List(updatedRecipe))
            completeResponse(response.asJson)
          case Failure(_) =>
            complete(StatusCodes.NotFound -> s"Recipe with id $recipeId not found.")
        }
      }
    }
  }

  val deleteRecipeById = path(PathMatchers.LongNumber) { recipeId =>
    delete {
      onComplete {
        RecipeService.deleteRecipeById(recipeId)
      } {
        case Success(_) =>
          val response = DeleteResponse(ResponseMessages.DELETE_SUCCESS)
          completeResponse(response.asJson)
        case Failure(_) =>
          val response = DeleteResponse(ResponseMessages.DELETE_FAILURE)
          completeResponse(response.asJson)
      }
    }
  }
}
