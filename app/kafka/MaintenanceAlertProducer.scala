package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json.Json
import models.Equipment

@Singleton
class MaintenanceAlertProducer @Inject()() {

  // Kafka configuration properties
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // Create Kafka producer
  private val producer = new KafkaProducer[String, String](props)

  // Method to send maintenance alerts
  def sendMaintenanceAlert(equipment: Equipment): Unit = {
    val alertMessage = Json.stringify(Json.toJson(equipment))
    val topic = "maintenance_alerts"

    // Log and send the message to Kafka
    val record = new ProducerRecord[String, String](topic, "alert", alertMessage)
    producer.send(record)
  }
}
