package com.lunatech.goldenalgo.onboarding

import java.util.UUID
import diode.Action

case class AppModel(recipe: Recipe)

object RecipeId {
  def random = UUID.randomUUID.toString
}

case object InitRecipe                    extends Action
case class UpdateRecipeName(name: String) extends Action
case object ResetRecipe                   extends Action
