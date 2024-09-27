import Domain.{RecipeBase, RecipeResponse}
import RecipeMain.db
import RecipeTableSchemas.{RecipeTable, Recipes, recipesBaseQuery}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Instant
import scala.concurrent.Future
import scala.util.{Failure, Success}

object RecipeStore {

  def createRecipe(recipe: RecipeBase): Future[RecipeTable]={

    val newRecipe = RecipeTable(title = recipe.title.getOrElse(""),
      making_time = recipe.making_time.getOrElse(""),
      serves = recipe.serves.getOrElse(""), ingredients = recipe.ingredients.getOrElse(""),
      cost = recipe.cost.getOrElse(0), created_at = Instant.now(), updated_at = Instant.now)


    val insertAction = recipesBaseQuery += newRecipe
    val insertFuture: Future[Int] = db.run(insertAction)

    println("CREATING NEW RECIPE")
    insertFuture.transform {
      case Success(value)=>
        println("Successfully created "+value)
        Success(newRecipe.copy(id = Some(value)))
      case scala.util.Failure(exception) =>
        println("Creation failed due to:  "+exception.getMessage)
        throw new NoSuchElementException
    }
  }

  def getAllRecipes(): Future[List[RecipeTable]]={
    // Fetch all recipes
    val fetchAllRecipesAction = recipesBaseQuery.result

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform{
      case Success(values) => Success(values.map(x=> x.copy()).toList)
      case exception: Exception => Failure(exception)
    }
  }

  def getRecipeById(id: Long): Future[RecipeTable] ={
    // Fetch all recipes
    val fetchAllRecipesAction = recipesBaseQuery.filter(_.id === id).result.headOption

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform{
      case Success(Some(value)) => Success(value)
      case Success(values) if values.isEmpty => throw new NoSuchElementException("Item not found")
      case exception: Exception => Failure(exception)
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
      case Success(value)=> Success(recipeToUpdate.copy(id = Some(value)))
      case scala.util.Failure(exception) => throw new NoSuchElementException
    }
  }

  def deleteRecipe(id: Long): Future[Int] ={
    // Fetch all recipes
    val fetchAllRecipesAction = recipesBaseQuery.filter(_.id === id).delete

    // Run the query
    val allRecipesFuture = db.run(fetchAllRecipesAction)

    allRecipesFuture.transform{
      case Success(value) => Success(value)
      case exception: Exception => Failure(exception)
    }
  }
}
