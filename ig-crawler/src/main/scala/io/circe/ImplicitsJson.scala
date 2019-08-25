package io.circe

import io.circe.Json.{JArray, JObject}

object ImplicitsJson {
  implicit class KeyValuesRecursiveUtilities(json: Json) {

    def mapByField(f: (String, Json) => (String, Json)): Json = json match {
      case JObject(value) =>
        Json.fromFields(value.toList.map {
          case (k, v) => f(k, v.mapByField(f))
        })
      case JArray(elems) => Json.fromValues(elems.map(_.mapByField(f)))
      case other         => f("", other)._2
    }

    def matchCondition(f: (String, Json) => Boolean): Boolean = json match {
      case JObject(value) =>
        value.toList.exists {
          case (k, v) => f(k, v) || v.matchCondition(f)
        }
      case JArray(elems) => elems.exists(_.matchCondition(f))
      case other         => f("", other)
    }

    def mapByFieldWithOptionalKey(f: (Option[String], Json) => (String, Json)): Json = json match {
      case JObject(value) =>
        Json.fromFields(value.toList.map {
          case (k, v) => f(Option(k), v.mapByFieldWithOptionalKey(f))
        })
      case JArray(elems) => Json.fromValues(elems.map(_.mapByFieldWithOptionalKey(f)))
      case other         => f(None, other)._2
    }

    def matchConditionWithOptionalKey(f: (Option[String], Json) => Boolean): Boolean = json match {
      case JObject(value) =>
        value.toList.exists {
          case (k, v) => f(Option(k), v) || v.matchConditionWithOptionalKey(f)
        }
      case JArray(elems) => elems.exists(_.matchConditionWithOptionalKey(f))
      case other         => f(None, other)
    }
  }
}
