import Domain.{RecipeFull, RecipeWithId, RecipeSubset}
import slick.jdbc.PostgresProfile.api._
import java.time.Instant


object RecipeTableSchemas {

  case class RecipeTable(id: Option[Long] = None,
                         title: String,
                         making_time: String,
                         serves: String,
                         ingredients: String,
                         cost: Long,
                         created_at: Instant,
                         updated_at: Instant) {
    def getRecipeWithAllFields(): RecipeFull = {
      RecipeFull(id, Some(title), Some(making_time), Some(serves), Some(ingredients), Some(cost), created_at, updated_at)
    }

    def getRecipeDetails(): RecipeWithId = {
      RecipeWithId(id, Some(title), Some(making_time), Some(serves), Some(ingredients), Some(cost))
    }

    def getRecipeSubset(): RecipeSubset = {
      RecipeSubset(Some(title), Some(making_time), Some(serves), Some(ingredients), Some(cost))
    }
  }


  class Recipes(tag: Tag) extends Table[RecipeTable](tag, "recipes") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def makingTime = column[String]("making_time")

    def serves = column[String]("serves")

    def ingredients = column[String]("ingredients")

    def cost = column[Long]("cost")

    def createdAt = column[Instant]("created_at")

    def updatedAt = column[Instant]("updated_at")

    // Projection that maps the table's columns to the Recipe case class

    // Projection to map table columns to Recipe case class
    def * = (id.?, title, makingTime, serves, ingredients, cost, createdAt, updatedAt) <> (RecipeTable.tupled, RecipeTable.unapply)
  }

  var recipesBaseQuery = TableQuery[Recipes]

}
