package zkpipe

import java.util

import com.typesafe.scalalogging.LazyLogging
import nl.grons.metrics.scala.{Histogram, Meter}
import org.apache.kafka.common.serialization.Serializer
import zkpipe.JsonSerializer.metrics

object RawSerializer {
    val SUBSYSTEM = "raw"

    val encodeRecords: Meter = metrics.meter("encoded-records", SUBSYSTEM)
    val encodeBytes: Meter = metrics.meter("encoded-bytes", SUBSYSTEM)
    val recordSize: Histogram = metrics.histogram("record-size", SUBSYSTEM)
}

class RawSerializer extends Serializer[LogRecord] with LazyLogging {
    import RawSerializer._

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def serialize(topic: String, record: LogRecord): Array[Byte] = {
        val bytes = record.bytes

        encodeRecords.mark()
        encodeBytes.mark(bytes.length)
        recordSize += bytes.length

        bytes
    }

    override def close(): Unit = {}
}