package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json.Json
import models.AllocationRequest

@Singleton
class OverdueReminderProducer @Inject()() {

  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  def sendOverdueReminder(allocationRequest: AllocationRequest): Unit = {
    val reminderMessage = Json.stringify(Json.toJson(allocationRequest))
    val topic = "overdue_reminders"
    val record = new ProducerRecord[String, String](topic, "reminder", reminderMessage)
    producer.send(record)
  }
}
