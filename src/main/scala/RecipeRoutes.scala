import Domain.{DeleteResponse, Recipe, RecipeBase, RecipeResponse, RecipeSubset, RequestExceptionResponse, RequestResponse, ResponseMessages}
import Exceptions.MissingFields
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, onComplete, path, pathPrefix}
import akka.http.scaladsl.server.{Directive1, MalformedRequestContentRejection, PathMatchers, Route}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.Directives._
import Domain.RecipeRequestImplicits._
import Domain.RecipeResponseImplicits._
import Domain.DeleteResponseImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import MarshallerImplicits.AkkaCirceSupport._
import io.circe.syntax._
import Domain.RequestResponseImplicits._
import Domain.RequestExceptionResponseImplicits._


object RecipeRoutes {

  val RECIPE_ROUTE = "recipes"

  val routes: Route = DebuggingDirectives.logRequest("recipes") {
    pathPrefix(RECIPE_ROUTE) {
      createRecipeRequest ~ getRecipeRequest ~ getRecipeById ~ updateRecipeById ~ deleteRecipeById
    }
  }

  val createRecipeRequest = post {
    entity(as[RecipeSubset]) { body =>
      val res = validateRecipe(body) match {
        case missingFields: MissingFields if missingFields.fields.isEmpty =>
          val insertedRecipeFuture = RecipeService.createRecipe(body)
          insertedRecipeFuture.transform {
            case Success(insertedRecipe) =>
              val res = RequestResponse[Recipe](ResponseMessages.CREATE_SUCCESS, List(insertedRecipe))
              Success(res.asJson)
          }
        case missingFields: MissingFields if missingFields.fields.nonEmpty =>
          val res = RequestExceptionResponse(ResponseMessages.CREATE_FAILURE, missingFields.fields.mkString("", ", ", ""))
          Future(res.asJson)
      }

      onComplete(res) {
        case Success(response) =>
          complete(HttpEntity(ContentTypes.`application/json`, response.noSpaces))
      }
    }
  }

  val getRecipeRequest = get {
    onComplete {
      val recipes = RecipeService.getAllRecipes()
      recipes.map(RequestResponse[RecipeResponse]("", _))
    } {
      case Success(resp) =>
        val response = resp.asJson.mapObject(_.remove("message"))
        complete(HttpEntity(ContentTypes.`application/json`, response.noSpaces))
    }
  }

  val getRecipeById = path(PathMatchers.LongNumber) { recipeId =>
    get {
      onComplete {
        val res = RecipeService.getRecipeById(recipeId)

        res.map(recipe => RequestResponse[RecipeResponse](
          ResponseMessages.GET_BY_ID_SUCCESS,
          List(recipe)))
      } {
        case Success(resp) =>
          complete(HttpEntity(ContentTypes.`application/json`, resp.asJson.toString))
      }
    }
  }

  val updateRecipeById = path(PathMatchers.LongNumber) { recipeId =>
    patch {
      entity(as[RecipeSubset]) { body =>
        onComplete {
          val res = RecipeService.updateRecipeById(recipeId, body)
          res.map(recipe => RequestResponse[RecipeSubset](
            ResponseMessages.UPDATE_SUCCESS,
            List(recipe)))
        } {
          case Success(resp) =>
            complete(HttpEntity(ContentTypes.`application/json`, resp.asJson.toString))
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
          val resp = DeleteResponse(ResponseMessages.DELETE_SUCCESS)
          complete(HttpEntity(ContentTypes.`application/json`, resp.asJson.toString))
        case Failure(_) =>
          val resp = DeleteResponse(ResponseMessages.DELETE_FAILURE)
          complete(HttpEntity(ContentTypes.`application/json`, resp.asJson.toString))
      }
    }
  }

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

}
