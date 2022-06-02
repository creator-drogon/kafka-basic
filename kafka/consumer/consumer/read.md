KafkaAutoConfiguration - is invoked as soon as app with kafka in  pom is run
KafkaProperties - Configuration properties for Spring for Apache Kafka, read from application.yml, includes bootstrap.properties

KafkaAnnotationDrivenConfiguration - kafkaListenerContainerFactory is configured here-- most imp for kafka listener 

The **KafkaListenerContainerFactory** is responsible to create the listener container for a particular endpoint.
Typical implementations, as the ConcurrentKafkaListenerContainerFactory used in the sample above,
provides the necessary configuration options that are supported by the underlying MessageListenerContainer.

`@KafkaListener` will use default implementation of KafkaListenerContainerFactory for container factory 

The annotation KafkaListener instantiates a facility called MessageListenerContainer, which handles parallelization, configuration, retries, and other things that the Kafka application requires, such as offset
Kafka Properties will use _buildConsumerProperties_ method to build the properties for our consumer annotated with Kafka listener