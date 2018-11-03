import net.scalax.asuna.mapper.common.annotations.RootModel

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object DTOTest02 extends App with DTOHelper02 {

  val ec = scala.concurrent.ExecutionContext.Implicits.global

  case class IdAndName(id: Int, name: String)
  def validateIdAndName = Future.successful(Right(IdAndName(1234, "name")))

  object Foo {
    @RootModel[IdAndName]
    val idAndName = validateIdAndName
    val age       = Future.successful(Right(6789))
  }
  case class Bar(id: Int, name: String, age: Int)

  val newModel = fe.effect(fe.singleModel[Bar](Foo).compile).data(ec)
  println(Await.result(newModel, Duration.Inf))

}
