
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.jdbc.PostgresProfile

import scala.util.{Failure, Success}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContextExecutor

object RecipeMain extends App with LazyLogging {
  val config = ConfigFactory.load()

  val pattern = "postgres://(.*):(.*)@(.*):(\\d+)/(.*)".r
  val pattern(username, password, host, port, dbName) = config.getString("databaseUrl.properties.url")

  // Create HikariCP configuration
  val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(s"jdbc:postgresql://$host:$port/$dbName")
  hikariConfig.setUsername(username)
  hikariConfig.setPassword(password)
  hikariConfig.setDriverClassName("org.postgresql.Driver")
  hikariConfig.setMaximumPoolSize(10)  // Customize based on your needs

  // Create and return a Slick Database instance with HikariCP
  val dataSource = new HikariDataSource(hikariConfig)
  val db = Database.forDataSource(dataSource, Some(10))

  implicit val system: ActorSystem = ActorSystem("recipe-server-system")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val serverPort = sys.env.getOrElse("PORT", "8080").toInt

  val bindingFuture = Http().newServerAt("0.0.0.0", serverPort).bindFlow(RecipeRoutes.routes)

  bindingFuture onComplete {
    case Success(answer) =>
      logger.info(s"Server online at http://localhost:$serverPort/\n")

    case Failure(msg) =>
      logger.error(s"Service failed: $msg, exiting")
      System.exit(1)
  }
}
