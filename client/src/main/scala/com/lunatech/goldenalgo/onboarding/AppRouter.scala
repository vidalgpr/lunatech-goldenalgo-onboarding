package com.lunatech.goldenalgo.onboarding

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router._

object AppRouter {

  sealed trait Page
  object Page {
    case object Home extends Page
    case object NotFound extends Page
  }

  val config: RouterConfig[Page] = RouterConfigDsl[Page].buildConfig { dsl =>

    import dsl._

    val counterConnection = AppCircuit.connect(_.recipe)
    val backend = new Backend(AppCircuit.zoom(x => x), AppCircuit)
    
    val homeRoute = staticRoute(root, Page.Home) ~> renderR(ctl => counterConnection(p => Home(backend, ctl)))
    val notFound = staticRoute("#notfound", Page.NotFound) ~> render(<.h2("NOT FOUND"))

    homeRoute.notFound { _ =>
      redirectToPage(Page.NotFound)(SetRouteVia.HistoryReplace)
    }
  }

  def router: Router[Page] =
    Router(BaseUrl.until_#, config)

}
