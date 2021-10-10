package com.lunatech.goldenalgo.onboarding

import diode._

import japgolly.scalajs.react._
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

object Home {

  case class Props(
      ctl: RouterCtl[AppRouter.Page]
  )

  def onFieldChange(field: String)(implicit backend: Backend): ReactEventFromInput => Callback = e =>
    Callback {
      field match {
        case "name"         => backend.setRecipeName(e.target.value)
        case "ingredients"  => backend.setRecipeIngredients(e.target.value)
        case "instructions" => backend.setRecipeInstructions(e.target.value)
        case _              =>
      }
    }

  def onSubmitButton()(implicit backend: Backend): Callback = for {
    _ <- Callback(backend.setRecipeId())
    _ <- Callback.alert("Recipe posted!")
    // _ <- Callback(backend.resetRecipe())
  } yield ()

  def component(implicit backend: Backend) =
    ScalaComponent
      .builder[Props]("Home")
      .render_P { p =>
        <.div(
          <.h2("My Awesome Recipe", ^.textAlign := "center"),
          <.br,
          <.p(
            "Current Recipe = ",
            <.b(backend.recipe.toString),
            ^.textAlign := "center"
          ),
          <.form(
            ^.textAlign := "center",
            ^.onSubmit --> onSubmitButton(),
            <.input.text(
              ^.onChange ==> onFieldChange("name"),
              ^.value := backend.recipe.name,
              ^.size  := 50
            ),
            <.br,
            <.input.text(
              ^.onChange ==> onFieldChange("ingredients"),
              ^.placeholder := "Ingredients: chickpeas, lettuce, ...",
              ^.value       := backend.recipe.ingredients.mkString(", "),
              ^.size        := 50
            ),
            <.br,
            <.input.text(
              ^.onChange ==> onFieldChange("instructions"),
              ^.placeholder := "Steps: 1, 2, 3, ...",
              ^.value       := backend.recipe.instructions.mkString(", "),
              ^.size        := 50
            ),
            <.br,
            <.input.submit()
          )
        )
      }
      .build

  def apply(
      backend: Backend,
      ctl: RouterCtl[AppRouter.Page]
  ): VdomElement = component(backend)(Props(ctl))
}
