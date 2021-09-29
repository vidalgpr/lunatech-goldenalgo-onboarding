package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

// Some scaffolding to get you started calling the backend
object Backend {

  def fetchRecipes(): Unit = Ajax.get("/recipes").onComplete {
    case Success(xhr) => decode[List[Recipe]](xhr.responseText)
    case Failure(t) => println("An error has occurred: " + t.getMessage)

  }
}
