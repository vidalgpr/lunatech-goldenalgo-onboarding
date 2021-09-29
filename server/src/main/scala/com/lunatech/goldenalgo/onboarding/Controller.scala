package com.lunatech.goldenalgo.onboarding

import akka.http.scaladsl.server.Directives._
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import io.circe.syntax._

class Controller()(implicit val ec: ExecutionContext) {

  // TODO: replace this hard-coded value by CRUD operations
  val recipe1 = Recipe("recipe1-id", "recipe1-name", Seq("ingredient1", "ingredient2"), Seq("instruction1", "instruction2"))

  val recipes = path("recipes") {
    get {
      complete(HttpEntity(ContentTypes.`application/json`, Seq(recipe1).asJson.noSpaces))
    }
  }

  val routes: Route = recipes
}
