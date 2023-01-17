package eu.xtrf.custom.clickup.clickupautomation

import com.google.pubsub.v1.TopicName
import eu.xtrf.custom.clickup.clickupautomation.config.TopicConfigurationProperties
import eu.xtrf.custom.clickup.clickupautomation.util.ClickupPublisher
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/clickupWebhookPublisher")
class ClickupWebhookPublisherController extends ClickupPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(this.simpleName)
    //randomly stopped working when private
    @Inject
    TopicConfigurationProperties topicConfigurationProperties

    @Value('${gcpProjectId}')
    String gcpProjectId

    @Post("/task/specification")
    HttpResponse<String> handleTaskCreatedInSpecificationsList(@Body Object body) {
        LOGGER.info("publishing request to /task/specification with ${body.toString()}")
        TopicName topicName = TopicName.ofProjectTopicName(
                gcpProjectId,
                topicConfigurationProperties.clickupTaskSpecificationCreated
        )
        doPublish(topicName, body)
    }

    @Post("/task/task")
    HttpResponse<String> handleTaskCreatedInTasksList(@Body Object body) {
        LOGGER.info("publishing request to /task/task with ${body.toString()}")
        TopicName topicName = TopicName.ofProjectTopicName(
                gcpProjectId,
                topicConfigurationProperties.clickupTaskTaskCreated
        )
        doPublish(topicName, body)
    }

    @Post("/task/bug")
    HttpResponse<String> handleTaskCreatedInBugsList(@Body Object body) {
        LOGGER.info("publishing request to /task/bug with ${body.toString()}")
        TopicName topicName = TopicName.ofProjectTopicName(
                gcpProjectId,
                topicConfigurationProperties.clickupTaskBugCreated
        )
        doPublish(topicName, body)
    }

    @Post("/task/updated")
    HttpResponse<String> handleTaskUpdated(@Body Object body) {
        LOGGER.info("publishing request to /task/updated with ${body.toString()}")
        TopicName topicName = TopicName.ofProjectTopicName(
                gcpProjectId,
                topicConfigurationProperties.clickupTaskUpdated
        )
        doPublish(topicName, body)
    }

    @Post("/task/retry")
    HttpResponse<String> retryTasks(@Body List<String> taskIds) {
        taskIds.each { String id ->
            Map taskCreatedEventDTO = [:]
            taskCreatedEventDTO.event = "taskCreated"
            taskCreatedEventDTO.task_id = id
            LOGGER.info("publishing request to /task/retry with ${taskCreatedEventDTO.toString()}")
            TopicName topicName = TopicName.ofProjectTopicName(
                    gcpProjectId,
                    topicConfigurationProperties.clickupTaskUpdated
            )
            doPublish(topicName, taskCreatedEventDTO)
        }
        HttpResponse.ok()
    }
}

