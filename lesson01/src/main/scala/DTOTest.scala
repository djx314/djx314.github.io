object DTOTest extends App with DTOHelper {
  case class Foo(name: String, id: Int, age: Int)
  case class Bar(id: Int, name: String, age: Int)

  val convert: Foo => Bar = { fooModel: Foo =>
    dto.effect(dto.singleModel[Bar](fooModel).compile).value
  }

  val foo = Foo("name", 1234, 6789)

  println(foo)
  println(convert(foo))

}
