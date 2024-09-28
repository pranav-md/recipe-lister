import Domain.{RecipeFull, RecipeBase, RecipeWithId, RecipeSubset}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RecipeService {

  def createRecipe(recipe: RecipeBase): Future[RecipeFull] = {
    val recipeRes = RecipeStore.createRecipe(recipe)
    recipeRes.map(_.getRecipeWithAllFields())
  }

  def getAllRecipes(): Future[List[RecipeWithId]] = {
    val recipeRes = RecipeStore.getAllRecipes()
    recipeRes.map(_.map(_.getRecipeDetails()))
  }

  def getRecipeById(recipeId: Long): Future[RecipeWithId] = {
    val recipeRes = RecipeStore.getRecipeById(recipeId)
    recipeRes.map(_.getRecipeDetails())
  }

  def updateRecipeById(recipeId: Long, recipe: RecipeBase): Future[RecipeSubset] = {
    val recipeRes = RecipeStore.updateRecipe(recipe, recipeId)
    recipeRes.map(_.getRecipeSubset())
  }

  def deleteRecipeById(recipeId: Long): Future[Int] = {
    for {
      _ <- RecipeStore.getRecipeById(recipeId)
      res <- RecipeStore.deleteRecipe(recipeId)
    }
    yield res
  }
}
