package com.lunatech.goldenalgo.onboarding

import io.circe._
import io.circe.generic.semiauto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

case class Recipe(id: String, name: String, ingredients: Seq[String], instructions: Seq[String])

object Recipe extends FailFastCirceSupport {
  implicit val codec: Codec[Recipe] = deriveCodec[Recipe]
}
