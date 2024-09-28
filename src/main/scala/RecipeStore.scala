import Domain.RecipeBase
import RecipeMain.db
import RecipeTableSchemas.{RecipeTable, recipesBaseQuery}
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Instant
import scala.concurrent.Future
import scala.util.{Failure, Success}

object RecipeStore extends LazyLogging {

  def createRecipe(recipe: RecipeBase): Future[RecipeTable] = {

    val newRecipe = RecipeTable(title = recipe.title.getOrElse(""),
      making_time = recipe.making_time.getOrElse(""),
      serves = recipe.serves.getOrElse(""), ingredients = recipe.ingredients.getOrElse(""),
      cost = recipe.cost.getOrElse(0), created_at = Instant.now(), updated_at = Instant.now)


    val insertAction = recipesBaseQuery += newRecipe
    val insertFuture: Future[Int] = db.run(insertAction)


    insertFuture.transform {
      case Success(value) =>
        logger.info(s"Successfully created the recipe with id: $value")
        Success(newRecipe.copy(id = Some(value)))
      case scala.util.Failure(exception) =>
        logger.error(s"Recipe creation failed: ${exception.getMessage}")
        throw new NoSuchElementException
    }
  }

  def getAllRecipes(): Future[List[RecipeTable]] = {
    // Fetch all recipes
    val fetchAllRecipesAction = recipesBaseQuery.result

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform {
      case Success(values) =>
        logger.info(s"Successfully fetched all recipes")
        Success(values.map(x => x.copy()).toList)
      case Failure(exception) => Failure(exception)
    }
  }

  def getRecipeById(id: Long): Future[RecipeTable] = {
    // Fetch required recipe with id
    val fetchAllRecipesAction = recipesBaseQuery.filter(_.id === id).result.headOption

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform {
      case Success(Some(value)) =>
        logger.info(s"Successfully fetched recipe: ${value.id.getOrElse(0)}")
        Success(value)
      case Success(values) if values.isEmpty =>
        logger.error(s"Recipe: $id not found")
        throw new NoSuchElementException("Item not found")
      case Failure(exception) => Failure(exception)
    }
  }

  def updateRecipe(recipe: RecipeBase, id: Long): Future[RecipeTable] = {
    // Fetch all recipes
    val recipeToUpdate = RecipeTable(Some(id), title = recipe.title.getOrElse(""),
      making_time = recipe.making_time.getOrElse(""),
      serves = recipe.serves.getOrElse(""), ingredients = recipe.ingredients.getOrElse(""),
      cost = recipe.cost.getOrElse(0), created_at = Instant.now(), updated_at = Instant.now)


    val fetchAllRecipesAction = recipesBaseQuery.filter(_.id === id).update(recipeToUpdate)

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform {
      case Success(value) =>
        logger.info(s"Successfully updated recipe: ${id}")
        Success(recipeToUpdate.copy(id = Some(value)))
      case scala.util.Failure(exception) =>
        logger.error(s"Updation for recipe: ${id} failed")
        throw new NoSuchElementException
    }
  }

  def deleteRecipe(id: Long): Future[Int] = {
    // Fetch all recipes
    val fetchAllRecipesAction = recipesBaseQuery.filter(_.id === id).delete

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform {
      case Success(value) =>
        logger.info(s"Successfully deleted recipe: ${id}")
        Success(value)
      case Failure(exception) =>
        logger.error(s"Encountered error while deleting recipe: ${id}. Message: ${exception.getMessage}")
        Failure(exception)
    }
  }
}
