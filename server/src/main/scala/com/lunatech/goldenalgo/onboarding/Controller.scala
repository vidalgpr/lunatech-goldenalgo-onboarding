package com.lunatech.goldenalgo.onboarding

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Route, StandardRoute }
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.model._
import io.circe.syntax._
import io.circe.parser.decode
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.{ RequestFailure, RequestSuccess }
import com.sksamuel.elastic4s.requests.update.UpdateResponse
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.Response
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import scala.concurrent.Future
import com.lunatech.goldenalgo.onboarding.adapter.DBConnector

class Controller()(
    implicit val ec: ExecutionContext,
    implicit val db: DBConnector,
    implicit val system: ActorSystem,
) {

  def toHttpEntity(payload: String) =
    HttpEntity(ContentTypes.`application/json`, payload)

  def getRecipesFromParameters(
      searchParams: Seq[(String, String)]
  ): StandardRoute =
    complete(
      db.multiSearchQuery(searchParams)
        .map(_.result.to[Recipe])
        .map(_.asJson.noSpaces)
        .map(toHttpEntity)
    )

  def outboundDataRecipeRoute(
      f: Recipe => Future[Response[Either[UpdateResponse, IndexResponse]]]
  ) = (request: HttpRequest, log: LoggingAdapter) => {
    val requestRecipe = Unmarshal(request).to[Recipe]
    onComplete(requestRecipe) {
      case Success(recipe) =>
        log.info(s"Got recipe: $recipe")
        complete(
          f(recipe).map {
            case RequestSuccess(status, body, headers, result) =>
              val docId = result.fold(_.id, _.id)
              log.info(s"successfully updated id=$docId")
              StatusCodes.Created

            case RequestFailure(status, body, headers, error) =>
              log.error(s"$status error updating recipe $error")
              StatusCodes.InternalServerError
          }
        )
      case Failure(ex) => failWith(ex)
    }
  }

  val getRecipeRoute: Route = get {
    concat(
      parameter("id") { recipeId =>
        getRecipesFromParameters(Seq(("_id", recipeId)))
      },
      parameter("name") { recipeName =>
        getRecipesFromParameters(Seq(("name", recipeName)))
      },
      parameters("ingredient".repeated) { ingredients =>
        getRecipesFromParameters(ingredients.map(i => ("ingredients", i)).toSeq)
      }
    )
  }

  lazy val getAllRecipesRoute: Route = (get & pathEndOrSingleSlash) {
    complete(
      db.searchAll()
        .map(_.result.to[Recipe])
        .map(_.asJson.noSpaces)
        .map(toHttpEntity)
    )
  }

  lazy val updateRecipeRoute =
    (put & pathEndOrSingleSlash & extractRequest & extractLog) {
      outboundDataRecipeRoute(r => db.updateDocumentById(r, r.id).map(_.map(Left(_))))
    }
  lazy val postRecipeRoute =
    (post & pathEndOrSingleSlash & extractRequest & extractLog) {
      outboundDataRecipeRoute(r => db.indexWithId(r, r.id).map(_.map(Right(_))))
    }

  lazy val deleteRecipeRoute: Route = delete {
    parameter("id") { id =>
      val deleteFuture = db.deleteDocumentById(id)
      onComplete(deleteFuture) {
        case Success(r)  => complete(StatusCodes.OK)
        case Failure(ex) => failWith(ex)
      }
    }
  }

  lazy val routes: Route = concat(
    path("api" / "recipe" / "get" / "all")(getAllRecipesRoute),
    path("api" / "recipe" / "get")(getRecipeRoute),
    path("api" / "recipe" / "post")(postRecipeRoute),
    path("api" / "recipe" / "delete")(deleteRecipeRoute),
    path("api" / "recipe" / "put")(updateRecipeRoute)
  )
}
