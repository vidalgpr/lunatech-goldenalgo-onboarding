package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._, io.circe.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import diode.Effect
import diode.EffectSingle

object BackendService {

  val base = "http://127.0.0.1:8080"

  def fetchRecipes(): Unit = Ajax.get("/recipes").onComplete {
    case Success(xhr) => decode[List[Recipe]](xhr.responseText)
    case Failure(t)   => println("An error has occurred: " + t.getMessage)
  }

  def fetchRecipeByIdEffect(id: String) = Effect(Ajax.get(s"$base/api/recipe/get?id=$id").map { xhr =>
    decode[Seq[Recipe]](xhr.responseText).fold(
      err => ResetRecipe,
      _ match {
        case Nil    => ResetRecipe
        case r :: c => SetRecipe(r)
      }
    )
  })

  def postRecipe(recipe: Recipe) = Effect(
    Ajax
      .post(
        s"$base/api/recipe/post",
        data = Ajax.InputData.str2ajax(recipe.asJson.noSpaces),
        headers = Map("Content-Type" -> "application/json")
      )
      .collect {
        case xhr if xhr.status == 200 => ResetRecipe
      }
  )

}
