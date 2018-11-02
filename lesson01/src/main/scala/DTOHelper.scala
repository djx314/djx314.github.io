import net.scalax.asuna.core.decoder.{DecoderShape, SplitData}
import net.scalax.asuna.mapper.common.RepColumnContent
import net.scalax.asuna.mapper.decoder.{DecoderContent, DecoderWrapperHelper}

trait DTOHelper {

  case class TupleHelper(head: Any, tail: TupleHelper)
  case class DTOResult[Out, T](value: T) extends DecoderContent[Out, T]

  object dto extends DecoderWrapperHelper[TupleHelper, TupleHelper, DTOResult] {
    override def effect[Rep, D, Out](rep: Rep)(implicit shape: DecoderShape.Aux[Rep, D, Out, TupleHelper, TupleHelper]): DTOResult[Out, D] = {
      val shape1  = shape
      val wrapCol = shape1.wrapRep(rep)
      val repCol  = shape1.buildRep(wrapCol, TupleHelper(null, null))
      val model   = shape1.takeData(wrapCol, repCol).current
      DTOResult[Out, D](model)
    }
  }

  implicit def dtoImplicit1[T]: DecoderShape.Aux[RepColumnContent[T, T], T, T, TupleHelper, TupleHelper] = {
    new DecoderShape[RepColumnContent[T, T], TupleHelper, TupleHelper] {
      override type Data   = T
      override type Target = T
      override def wrapRep(base: => RepColumnContent[T, T]): T                       = base.rep
      override def buildRep(base: T, oldRep: TupleHelper): TupleHelper               = TupleHelper(base, oldRep)
      override def takeData(rep: T, oldData: TupleHelper): SplitData[T, TupleHelper] = SplitData(oldData.head.asInstanceOf[T], oldData.tail)
    }
  }

}
