import io.circe.{Encoder, Json}

object CirceTest extends App with CirceHelper {

  case class Bar(id: Int, name: String, age: Int)

  object Foo {}

  val bar1  = Bar(478848, "name", 8237)
  val json1 = circe.effect(circe.singleModel[Bar](Foo).compile).toJson(bar1)
  val json2 = Json.fromJsonObject(json1)
  println(json2.noSpaces)

  val json3 = {
    import io.circe.syntax._
    import io.circe.generic.auto._
    bar1.asJson
  }
  println(json2 == json3)

  case class CompareBar(id: String, name: String, age: Int)
  object Foo2 {
    val id = Encoder.encodeInt.contramap { str: String =>
      str.toInt
    }
  }

  val bar2 = CompareBar("478848", "name", 8237)

  val json4 = circe.effect(circe.singleModel[CompareBar](Foo2).compile).toJson(bar2)
  val json5 = Json.fromJsonObject(json4)
  println(json5.noSpaces)
  println(json5 == json3)

  object Foo3 {
    val id = ""
  }

  val json6 = circe.effect(circe.debugSingleModel[CompareBar](Foo3)).toJson(bar2)
  circe.singleModel[CompareBar](Foo3).debugCompile.i3(circe)

}
