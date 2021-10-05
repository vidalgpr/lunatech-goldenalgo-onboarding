/*
 * Copyright Audi Electronics Venture GmbH 2019
 */

package com.lunatech.goldenalgo.onboarding

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import com.lunatech.goldenalgo.onboarding.adapter.DBConnector
import akka.stream.Materializer

object Main {

  def main(args: Array[String]) = {

    val env = sys.env
    val dbHost = env.get("DB_HOST")
    val dbIndex = env.get("DB_INDEX") //required

    implicit val system: ActorSystem = ActorSystem("main-system")
    implicit val materializer: Materializer = Materializer(system)
    implicit val ec: ExecutionContext = system.dispatcher
    
    implicit val dbClient = new DBConnector(dbHost, dbIndex)
    val controller = new Controller()

    new WebServer(controller)
      .bind()
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }
}
