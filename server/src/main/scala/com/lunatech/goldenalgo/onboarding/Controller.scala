package com.lunatech.goldenalgo.onboarding

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import io.circe.syntax._
import com.sksamuel.elastic4s.circe._
import com.lunatech.goldenalgo.onboarding.adapter.DBConnector
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}

class Controller()(
    implicit val ec: ExecutionContext,
    implicit val dbClient: DBConnector
) {

  // TODO: replace this hard-coded value by CRUD operations
  val recipe1 = Recipe(
    "recipeyyyyyy1-id",
    "recipeyyyyyy1-name",
    Seq("ingredient1", "ingredient2"),
    Seq("instruction1", "instruction2")
  )

  val recipes = path("recipes") {
    parameter(Symbol("id")) { (recipeId: String) =>
      get {
        val response = dbClient.queryIdx(recipeId)
        val result = response.map(_.result.to[Recipe])

        onComplete(result) {
          case Success(r) if r.length == 0 => complete(StatusCodes.NotFound)
          case Success(r) =>
            complete(
              HttpEntity(ContentTypes.`application/json`, r.asJson.noSpaces)
            )
          case Failure(e) => complete(StatusCodes.BadRequest, e.getMessage)
        }
      }
    }
  }

  val postRecipe = path("recipe" / "test_upload") {
    get {
      dbClient.initIdx()
      dbClient.idxInto(recipe1)

      complete(StatusCodes.OK)
    }
  }

  val routes: Route = recipes ~ postRecipe
}
