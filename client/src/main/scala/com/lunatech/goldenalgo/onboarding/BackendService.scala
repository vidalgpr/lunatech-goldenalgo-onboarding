package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._, io.circe.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import diode.Effect
import diode.EffectSingle

object BackendService {

  val base = "http://127.0.0.1:8080"

  def fetchAllRecipes() = Effect(Ajax.get(s"$base/api/recipe/get/all").map { xhr =>
    decode[Seq[Recipe]](xhr.responseText).fold(
      err => ResetRecipe,
      _ match {
        case Nil => ResetRecipe
        case rs  => SetMultipleRecipes(rs)
      }
    )
  })

  def fetchRecipeByIdEffect(id: String) = Effect(Ajax.get(s"$base/api/recipe/get?id=$id").map { xhr =>
    decode[Seq[Recipe]](xhr.responseText).fold(
      err => ResetRecipe,
      _ match {
        case Nil    => ResetRecipe
        case r :: c => SetSingleRecipe(r)
      }
    )
  })

  def postRecipe(recipe: Option[Recipe]) =
    Effect(
      Ajax
        .post(
          s"$base/api/recipe/post",
          data = Ajax.InputData.str2ajax(recipe.getOrElse(Recipe.empty).asJson.noSpaces),
          headers = Map("Content-Type" -> "application/json")
        )
        .collect {
          case xhr if xhr.status == 200 => ResetRecipe
        }
    )

  def updateRecipe(recipe: Option[Recipe]) =
    Effect(
      Ajax
        .put(
          s"$base/api/recipe/put",
          data = Ajax.InputData.str2ajax(recipe.getOrElse(Recipe.empty).asJson.noSpaces),
          headers = Map("Content-Type" -> "application/json")
        )
        .collect {
          case xhr if xhr.status == 200 => ResetRecipe
        }
    )

  def deleteRecipe(recipe: Option[Recipe]) = Effect(
    Ajax
      .delete(s"$base/api/recipe/delete?id=${recipe.getOrElse(Recipe.empty).id}")
      .collect {
        case xhr if xhr.status == 200 => ResetRecipe
      }
  )

}
