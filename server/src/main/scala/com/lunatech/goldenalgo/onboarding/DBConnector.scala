package com.lunatech.goldenalgo.onboarding.adapter

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{Indexable, Response}
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.{ElasticRequest, ElasticClient}
import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import com.sksamuel.elastic4s.requests.indexes.CreateIndexResponse
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

  def initIdx(): Response[CreateIndexResponse] = client.execute {
    createIndex(idx)
  }.await

  def idxInto[T](input: T)(implicit c: Indexable[T]) = client.execute {
    indexInto(idx)
      .doc(input)
      .refresh(rp)
  }

  def queryIdx(keyword: String): Future[Response[SearchResponse]] =
    client.execute {
      search(idx).query(keyword)
    }

  def matchQueryIdx(field: String, keyword: String): Future[Response[SearchResponse]] =
    client.execute {
      search(idx).matchQuery(field, keyword)
    }

  def close(): Unit = client.close()

}
