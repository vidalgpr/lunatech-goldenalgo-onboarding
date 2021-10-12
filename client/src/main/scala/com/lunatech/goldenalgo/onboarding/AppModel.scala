package com.lunatech.goldenalgo.onboarding

import java.util.UUID
import diode.Action
import diode.data.Pot

case class AppModel(recipe: Option[Recipe])

object RecipeId {
  def random = UUID.randomUUID.toString
}

case object InitRecipe                         extends Action
case object ResetRecipe                        extends Action
case class SetId(id: String = RecipeId.random) extends Action
case class SetRecipe(recipe: Recipe)           extends Action
case class LoadRecipe(id: String)              extends Action
// case class UpdateRecipeName(id: String, name: String)                      extends Action
// case class UpdateRecipeIngredients(id: String, ingredients: Seq[String])   extends Action
// case class UpdateRecipeInstructions(id: String, instructions: Seq[String]) extends Action
