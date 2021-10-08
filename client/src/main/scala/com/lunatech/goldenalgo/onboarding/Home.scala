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

  case class State(editText: String)

  def component(backend: Backend) =
    ScalaComponent
      .builder[Props]("Home")
      .render_P { p =>
        <.div(
          <.h3("My Awesome Recipe"),
          <.p("RecipeName = ", <.b(backend.getRecipe.toString)),
          <.button(
            ^.onClick --> {
              CallbackTo(backend.updateRecipeName())
            },
            "UpdateName"
          ),
          <.button(
            ^.onClick --> {
              CallbackTo(backend.resetRecipe())
            },
            "ResetRecipe"
          )
        )
      }
      .build

  def render(p: Props, s: State): VdomElement = ???

  def apply(
      backend: Backend,
      ctl: RouterCtl[AppRouter.Page]
  ): VdomElement = component(backend)(Props(ctl))
}
