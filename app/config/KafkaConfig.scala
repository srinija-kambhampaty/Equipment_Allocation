package config

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import java.util.Properties

object KafkaConfig {
  val brokerList = "localhost:9092" // Replace with your Kafka broker address

  def producerProps: Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props
  }
}
