import net.scalax.asuna.core.decoder.{DecoderShape, SplitData}
import net.scalax.asuna.mapper.common.RepColumnContent
import net.scalax.asuna.mapper.decoder.{DecoderContent, DecoderWrapperHelper}

import scala.concurrent.{ExecutionContext, Future}

trait DTOHelper02 {

  case class WrapHelper(ec: ExecutionContext, data: Future[Either[Exception, (Any, Any)]])

  trait FEWrapper[RepOut, DataType] extends DecoderContent[RepOut, DataType] {
    def data(implicit ec: ExecutionContext): Future[Either[Exception, DataType]]
  }

  object fe extends DecoderWrapperHelper[WrapHelper, (Any, Any), FEWrapper] {
    override def effect[Rep, D, Out](rep: Rep)(implicit shape: DecoderShape.Aux[Rep, D, Out, WrapHelper, (Any, Any)]): FEWrapper[Out, D] = {
      val shape1  = shape
      val wrapCol = shape1.wrapRep(rep)
      new FEWrapper[Out, D] {
        override def data(implicit ec: ExecutionContext): Future[Either[Exception, D]] = {
          val wrap = shape1.buildRep(wrapCol, WrapHelper(ec, Future.successful(Right(((), ())))))
          wrap.data.map(r => r.right.map(d1 => shape1.takeData(wrapCol, d1).current))(ec)
        }
      }
    }
  }

  implicit def feImplicit1[T]: DecoderShape.Aux[RepColumnContent[Future[Either[Exception, T]], T], T, Future[Either[Exception, T]], WrapHelper, (Any, Any)] = {
    new DecoderShape[RepColumnContent[Future[Either[Exception, T]], T], WrapHelper, (Any, Any)] {
      override type Data   = T
      override type Target = Future[Either[Exception, T]]
      override def wrapRep(base: => RepColumnContent[Future[Either[Exception, T]], T]): Future[Either[Exception, T]] = base.rep
      override def buildRep(base: Future[Either[Exception, T]], oldRep: WrapHelper): WrapHelper = {
        val either = oldRep.data.flatMap(s => base.map(d => d.right.flatMap(d1 => s.right.map(s1 => (d1, s1): (Any, Any))))(oldRep.ec))(oldRep.ec)
        WrapHelper(oldRep.ec, either)
      }
      override def takeData(rep: Future[Either[Exception, T]], oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(oldData._1.asInstanceOf[T], oldData._2.asInstanceOf[(Any, Any)])
    }
  }

  implicit def feImplicit2[T]: DecoderShape.Aux[RepColumnContent[Either[Exception, T], T], T, Either[Exception, T], WrapHelper, (Any, Any)] = {
    new DecoderShape[RepColumnContent[Either[Exception, T], T], WrapHelper, (Any, Any)] {
      override type Data   = T
      override type Target = Either[Exception, T]
      override def wrapRep(base: => RepColumnContent[Either[Exception, T], T]): Either[Exception, T] = base.rep
      override def buildRep(base: Either[Exception, T], oldRep: WrapHelper): WrapHelper = {
        val either = oldRep.data.map(s => base.right.flatMap(d => s.right.map(s1 => (d, s1): (Any, Any))))(oldRep.ec)
        WrapHelper(oldRep.ec, either)
      }
      override def takeData(rep: Either[Exception, T], oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(oldData._1.asInstanceOf[T], oldData._2.asInstanceOf[(Any, Any)])
    }
  }

  implicit def feImplicit3[T]: DecoderShape.Aux[RepColumnContent[Future[T], T], T, Future[T], WrapHelper, (Any, Any)] = {
    new DecoderShape[RepColumnContent[Future[T], T], WrapHelper, (Any, Any)] {
      override type Data   = T
      override type Target = Future[T]
      override def wrapRep(base: => RepColumnContent[Future[T], T]): Future[T] = base.rep
      override def buildRep(base: Future[T], oldRep: WrapHelper): WrapHelper = {
        val either = oldRep.data.flatMap(s => base.map(d => s.right.map(s1 => (d, s1): (Any, Any)))(oldRep.ec))(oldRep.ec)
        WrapHelper(oldRep.ec, either)
      }
      override def takeData(rep: Future[T], oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(oldData._1.asInstanceOf[T], oldData._2.asInstanceOf[(Any, Any)])
    }
  }

  implicit def feImplicit4[T]: DecoderShape.Aux[RepColumnContent[T, T], T, T, WrapHelper, (Any, Any)] = {
    new DecoderShape[RepColumnContent[T, T], WrapHelper, (Any, Any)] {
      override type Data   = T
      override type Target = T
      override def wrapRep(base: => RepColumnContent[T, T]): T = base.rep
      override def buildRep(base: T, oldRep: WrapHelper): WrapHelper = {
        val either = oldRep.data.map(s => s.right.map(s1 => (base, s1): (Any, Any)))(oldRep.ec)
        WrapHelper(oldRep.ec, either)
      }
      override def takeData(rep: T, oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(oldData._1.asInstanceOf[T], oldData._2.asInstanceOf[(Any, Any)])
    }
  }

}
