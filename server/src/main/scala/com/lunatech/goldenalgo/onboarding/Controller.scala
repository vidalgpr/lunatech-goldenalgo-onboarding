package com.lunatech.goldenalgo.onboarding

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import io.circe.syntax._
import io.circe.parser.decode
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.{RequestSuccess, RequestFailure}
import com.lunatech.goldenalgo.onboarding.adapter.DBConnector
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Success, Failure}

class Controller()(
    implicit val ec: ExecutionContext,
    implicit val db: DBConnector,
    implicit val system: ActorSystem,
    implicit val matz: Materializer
) {

  def toHttpEntity(payload: String) =
    HttpEntity(ContentTypes.`application/json`, payload)

  db.initIdx()

  val getRecipeRoute: Route = {
    parameter(Symbol("id")) { (recipeId: String) =>
      get {
        complete(
          db.matchQueryIdx("id", recipeId)
            .map(_.result.to[Recipe])
            .map(_.asJson.noSpaces)
            .map(toHttpEntity)
        )
      }
    } ~ parameter(Symbol("name")) { (recipeName: String) =>
      get {
        complete(
          db.matchQueryIdx("name", recipeName)
            .map(_.result.to[Recipe])
            .map(_.asJson.noSpaces)
            .map(toHttpEntity)
        )
      }
    }
  }

  lazy val postRecipeRoute = {
    (post & pathEndOrSingleSlash & extractRequest & extractLog) {
      (request, log) =>
        val entity = request.entity
        val strictEntity = entity.toStrict(2.seconds)
        val recipe = strictEntity
          .map(_.data.utf8String)
          .map(decode[Recipe](_))

        onComplete(recipe) {
          _.flatMap(_.toTry) match {
            case Success(recipe) =>
              log.info(s"Got recipe: $recipe")

              complete(
                db.idxInto(recipe).map {
                  case RequestSuccess(_, _, _, _) => StatusCodes.OK
                  case RequestFailure(_, _, _, _) => StatusCodes.InternalServerError
                }
              )

            case Failure(ex) => failWith(ex)
          }
        }
    }
  }

  lazy val routes: Route = concat(
    path("api" / "recipes")(getRecipeRoute),
    path("recipe" / "test_upload")(postRecipeRoute)
  )
}
