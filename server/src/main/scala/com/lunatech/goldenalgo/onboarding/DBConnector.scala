package com.lunatech.goldenalgo.onboarding.adapter

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{Indexable, Response}
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.{ElasticRequest, ElasticClient}
import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.requests.searches.{
  SearchResponse,
  MultiSearchResponse
}
import com.sksamuel.elastic4s.requests.delete.DeleteResponse
import com.sksamuel.elastic4s.requests.update.UpdateResponse
import scala.concurrent.Future

class DBConnector(host: Option[String], index: Option[String])(implicit
    val system: ActorSystem
) {
  import com.sksamuel.elastic4s.ElasticDsl._

  lazy val defaultHost = "127.0.0.1:9200"
  lazy val defaultIndex = "recipes"
  val rp = RefreshPolicy.Immediate

  val idx = index.getOrElse(defaultIndex)

  lazy val client = ElasticClient(
    AkkaHttpClient(
      AkkaHttpClientSettings(Seq(host.getOrElse(host.getOrElse(defaultHost))))
    )
  )

  def indexWithId[T](input: T, id: String)(implicit c: Indexable[T]) =
    client.execute {
      indexInto(idx)
        .id(id)
        .doc(input)
        .refresh(rp)
    }

  def searchAll(): Future[Response[SearchResponse]] =
    client.execute {
      search(idx)
    }

  def termQuerySearch(
      field: String,
      keyword: String
  ): Future[Response[SearchResponse]] =
    client.execute {
      search(idx).termQuery(field, keyword)
    }

  def multiSearchQuery(
      searchFor: Seq[(String, String)]
  ): Future[Response[MultiSearchResponse]] = client.execute {
    multi(
      for (searchParameter <- searchFor)
        yield search(idx).termQuery(searchParameter)
    )
  }

  def deleteDocumentById(id: String): Future[Response[DeleteResponse]] =
    client.execute {
      deleteById(idx, id)
    }

  def updateDocumentById[T](input: T, id: String)(implicit
      c: Indexable[T]
  ): Future[Response[UpdateResponse]] =
    client.execute {
      updateById(idx, id)
        .doc(input)
        .refresh(rp)
    }

  def close(): Unit = client.close()

}
