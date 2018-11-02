import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object DTOTest02 extends App with DTOHelper02 {
  object Foo {
    val name = Future.successful("name")
    val id   = Right(1234)
    val age  = Future.successful(Right(6789))
  }
  case class Bar(id: Int, name: String, age: Int)

  val ec = scala.concurrent.ExecutionContext.Implicits.global

  val newModel: Future[Either[Exception, Bar]] =
    fe.effect(fe.singleModel[Bar](Foo).compile).data(ec)

  println(Await.result(newModel, Duration.Inf))

}
