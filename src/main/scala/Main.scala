
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.jdbc.PostgresProfile

import scala.util.{Failure, Success}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  val pattern = "postgres://(.*):(.*)@(.*):(\\d+)/(.*)".r
  val pattern(username, password, host, port, dbName) = "postgres://u2gs3teph4c6r1:p62927feff907726727bb3e864fd1fc592ba5ee51b809106ef0c0097e59b00c1f@cbec45869p4jbu.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d7b32tv8s2pjet"

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

  val bindingFuture = Http().newServerAt("localhost", 8080).bindFlow(RecipeRoutes.routes)

  bindingFuture onComplete {
    case Success(answer) =>
      println(s"Server online at http://localhost:8081/\n")

    case Failure(msg) =>
      println(s"Service failed: $msg, exiting")
      System.exit(1)
  }
}
