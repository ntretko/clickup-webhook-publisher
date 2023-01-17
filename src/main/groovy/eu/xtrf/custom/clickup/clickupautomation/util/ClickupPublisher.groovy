package eu.xtrf.custom.clickup.clickupautomation.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.core.ApiFuture
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import io.micronaut.http.HttpResponse
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

class ClickupPublisher {
    private final static Logger LOGGER = LoggerFactory.getLogger(this.simpleName)
    ObjectMapper objectMapper = new ObjectMapper()

    protected HttpResponse<String> doPublish(TopicName topicName, Object body) {
        Publisher publisher
        try {
            publisher = Publisher.newBuilder(topicName).build()
            String bodyAsString = objectMapper.writeValueAsString(body)
            ByteString messageAsByte = ByteString.copyFromUtf8(bodyAsString)
            PubsubMessage message = PubsubMessage.newBuilder().setData(messageAsByte).build()

            ApiFuture<String> response = publisher.publish(message)
            String responseMessage = response.get()
            String msg = "Message ID: $responseMessage - Successfully published message"
            LOGGER.info(msg)
            HttpResponse.ok(msg)
        } finally {
            if (publisher) {
                publisher.shutdown()
                publisher.awaitTermination(1, TimeUnit.MINUTES)
            }
        }
    }
}
