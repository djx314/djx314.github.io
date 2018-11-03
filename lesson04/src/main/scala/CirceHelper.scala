import io.circe.{Encoder, Json, JsonObject}
import net.scalax.asuna.core.common.Placeholder
import net.scalax.asuna.core.decoder.{DecoderShape, SplitData}
import net.scalax.asuna.core.encoder.EncoderShape
import net.scalax.asuna.mapper.common.RepColumnContent
import net.scalax.asuna.mapper.decoder.{DecoderContent, DecoderWrapperHelper}
import net.scalax.asuna.mapper.encoder.{EncoderContent, EncoderWrapperHelper}

import scala.concurrent.{ExecutionContext, Future}

trait CirceHelper {

  trait EncoderWrapper {
    type DataType
    val encoder: Encoder[DataType]
    val key: String
  }

  trait EncoderWrapperImpl[T] extends EncoderWrapper {
    override type DataType = T
  }

  object EncoderWrapper {
    def apply[D](key: String, encoder: Encoder[D]): EncoderWrapperImpl[D] = {
      val encoder1 = encoder
      val key1     = key

      new EncoderWrapperImpl[D] {
        override val encoder = encoder1
        override val key     = key1
      }
    }
  }

  trait CirceWrapper[RepOut, DataType] extends EncoderContent[RepOut, DataType] {
    def toJson(data: DataType): JsonObject
  }

  object circe extends EncoderWrapperHelper[List[EncoderWrapper], List[(String, Json)], CirceWrapper] {
    override def effect[Rep, D, Out](rep: Rep)(
      implicit shape: EncoderShape.Aux[Rep, D, Out, List[EncoderWrapper], List[(String, Json)]]): CirceWrapper[Out, D] = {
      val shape1  = shape
      val wrapCol = shape1.wrapRep(rep)
      new CirceWrapper[Out, D] {
        override def toJson(data: D): JsonObject = {
          val dataList = shape1.buildData(data, wrapCol, List.empty)

          JsonObject.fromIterable(dataList)
        }
      }
    }
  }

  implicit def feImplicit1[T](implicit encoder: Encoder[T])
    : EncoderShape.Aux[RepColumnContent[Placeholder[T], T], T, EncoderWrapperImpl[T], List[EncoderWrapper], List[(String, Json)]] = {
    new EncoderShape[RepColumnContent[Placeholder[T], T], List[EncoderWrapper], List[(String, Json)]] {
      override type Data   = T
      override type Target = EncoderWrapperImpl[T]
      override def wrapRep(base: => RepColumnContent[Placeholder[T], T]): EncoderWrapperImpl[T] =
        EncoderWrapper(base.columnInfo.tableColumnSymbol.name, encoder)
      override def buildRep(base: EncoderWrapperImpl[T], oldRep: List[EncoderWrapper]): List[EncoderWrapper] = base :: oldRep
      override def buildData(data: T, rep: EncoderWrapperImpl[T], oldData: List[(String, Json)]): List[(String, Json)] =
        ((rep.key, rep.encoder(data))) :: oldData
    }
  }

  implicit def feImplicit2[T]: EncoderShape.Aux[RepColumnContent[Encoder[T], T], T, EncoderWrapperImpl[T], List[EncoderWrapper], List[(String, Json)]] = {
    new EncoderShape[RepColumnContent[Encoder[T], T], List[EncoderWrapper], List[(String, Json)]] {
      override type Data   = T
      override type Target = EncoderWrapperImpl[T]
      override def wrapRep(base: => RepColumnContent[Encoder[T], T]): EncoderWrapperImpl[T] = {
        val rep = base
        EncoderWrapper(rep.columnInfo.tableColumnSymbol.name, rep.rep)
      }
      override def buildRep(base: EncoderWrapperImpl[T], oldRep: List[EncoderWrapper]): List[EncoderWrapper] = base :: oldRep
      override def buildData(data: T, rep: EncoderWrapperImpl[T], oldData: List[(String, Json)]): List[(String, Json)] =
        ((rep.key, rep.encoder(data))) :: oldData
    }
  }

}
