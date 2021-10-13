package com.lunatech.goldenalgo.onboarding

import diode.Action

case class AppModel(recipes: Seq[Recipe])

case object InitRecipe                              extends Action
case object ResetRecipe                             extends Action
case class SetSingleRecipe(recipe: Recipe)          extends Action
case class SetMultipleRecipes(recipes: Seq[Recipe]) extends Action
case class SetNewRecipe(recipe: Recipe)             extends Action
case class LoadSingleRecipe(id: String)             extends Action
case object PostRecipe                              extends Action
case object UpdateRecipe                            extends Action
case object DeleteRecipe                            extends Action
case object LoadAllRecipes                          extends Action
