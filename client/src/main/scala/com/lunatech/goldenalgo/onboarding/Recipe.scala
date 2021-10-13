package com.lunatech.goldenalgo.onboarding

import java.util.UUID
import io.circe._, io.circe.parser._
import io.circe.generic.semiauto._

case class Recipe(id: String, name: String, ingredients: Seq[String], instructions: Seq[String])

object Recipe {
  implicit val codec: Codec[Recipe] = deriveCodec[Recipe]
  val empty = Recipe("", "", Nil, Nil)
}

object RecipeId {
  def random = UUID.randomUUID.toString
}

sealed trait RecipeField
case object RecipeName         extends RecipeField
case object RecipeIngredients  extends RecipeField
case object RecipeInstructions extends RecipeField
