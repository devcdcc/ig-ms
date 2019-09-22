package com.github.devcdcc.crawler.consumer.builder.processor

import io.circe.Json

sealed trait JsonFlatter {
  protected def flatter(json: Json): Iterable[Json]
}

trait MediaJsonFlatter extends JsonFlatter {

  protected def flatter(json: Json): Iterable[Json] =
    json.hcursor.downField("items").focus.flatMap(_.asArray).getOrElse(Iterable.empty)
}

trait FollowersJsonFlatter extends JsonFlatter {

  protected def flatter(json: Json): Iterable[Json] =
    json.hcursor.downField("users").focus.flatMap(_.asArray).getOrElse(Iterable.empty)
}
