package com.lunatech.goldenalgo.onboarding

import java.util.UUID
import diode.Action

case class AppModel(recipe: Recipe)

object RecipeId {
  def random = UUID.randomUUID.toString
}

case object InitRecipe                                      extends Action
case object ResetRecipe                                     extends Action
case class SetRecipeId(id: String = RecipeId.random)        extends Action
case class SetRecipeName(name: String)                      extends Action
case class SetRecipeIngredients(ingredients: Seq[String])   extends Action
case class SetRecipeInstructions(instructions: Seq[String]) extends Action
case class SetRecipe(recipe: Recipe)                        extends Action
