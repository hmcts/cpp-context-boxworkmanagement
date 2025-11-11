package uk.gov.moj.cpp.boxworkmanagement.helper;


import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.messaging.QueueUriProvider.queueUri;
import static uk.gov.moj.cpp.boxworkmanagement.helper.CamundaHelper.getRunningProcessInstanceId;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.justice.services.messaging.MetadataBuilder;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.json.JsonObject;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class JMSTopicHelper implements AutoCloseable {

    public static final String USER_ID = UUID.randomUUID().toString();
    private static final String BOXWORKAPPLICATION =  "boxworkApplication";

    private static final String QUEUE_URI = queueUri();
    private Session session;
    private MessageProducer messageProducer;
    private Connection connection;

    public static void postMessageToStartBoxwork(final String payload, final String publicEventName) {
        final StringToJsonObjectConverter stringToJsonObjectConverter = new StringToJsonObjectConverter();

        try (JMSTopicHelper publicTopicHelper = new JMSTopicHelper(); ) {
            publicTopicHelper.startProducer("jms.topic.public.event");
            publicTopicHelper.sendMessage(publicEventName, stringToJsonObjectConverter.convert(payload), empty());
          }
    }

    public static void verifyMessageToStartAndCompletedBoxwork(final String payload1, final String payload2, final String publicEventName1, final String publicEventName2) {
        final String applicationBusinessKey = randomUUID().toString();
        final StringToJsonObjectConverter stringToJsonObjectConverter = new StringToJsonObjectConverter();
         String boxworkApplicationGeneratedPayload = payload1.replace("URN_DEFAULT_VALUE", applicationBusinessKey);

            try (JMSTopicHelper publicTopicHelper = new JMSTopicHelper()) {
                publicTopicHelper.startProducer("public.event");
                publicTopicHelper.sendMessage(publicEventName1, stringToJsonObjectConverter.convert(boxworkApplicationGeneratedPayload), empty());

                 String processInstanceId = getRunningProcessInstanceId(applicationBusinessKey,BOXWORKAPPLICATION) ;

                assertNotNull(processInstanceId, "ProcessInstanceId should not be null");

                 boxworkApplicationGeneratedPayload = payload2.replace("URN_DEFAULT_VALUE", applicationBusinessKey);
                publicTopicHelper.sendMessage(publicEventName2, stringToJsonObjectConverter.convert(boxworkApplicationGeneratedPayload), empty());
                  processInstanceId = getRunningProcessInstanceId(applicationBusinessKey,BOXWORKAPPLICATION) ;

                assertNull(processInstanceId, "ProcessInstanceId should be null");

            }

    }

    public void startProducer(final String topicName) {

        try {
            final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(QUEUE_URI);
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            final Destination destination = session.createTopic(topicName);
            messageProducer = session.createProducer(destination);
        } catch (final JMSException e) {
            close();
            throw new RuntimeException(format("Failed to create message producer to topic: '%s', queue uri: '%s'", topicName, QUEUE_URI), e);
        }
    }

    public void sendMessage(final String commandName, final JsonObject payload, Optional<UUID> streamId) {

        if (messageProducer == null) {
            close();
            throw new RuntimeException("Message producer not started. Please call startProducer(...) first.");
        }

        final Metadata metadata = getMetadataWithName(commandName, streamId);

        sendMessage(commandName, payload, metadata);
    }

    private Metadata getMetadataWithName(String commandName, Optional<UUID> streamId) {
        MetadataBuilder metadataBuilder = Envelope.metadataBuilder().withId(UUID.randomUUID())
                .withName(commandName)
                .createdAt(ZonedDateTime.now())
                .withUserId(USER_ID)
                .withClientCorrelationId(UUID.randomUUID().toString());

        if (streamId.isPresent()) {
            metadataBuilder = metadataBuilder.withStreamId(streamId.get())
                    .withVersion(1);
        }

        return metadataBuilder.build();
    }

    private void sendMessage(String commandName, JsonObject payload, Metadata metadata) {
        final JsonEnvelope jsonEnvelope = envelopeFrom(metadata, payload);
        final String json = jsonEnvelope.toDebugStringPrettyPrint();

        try {
            final TextMessage message = session.createTextMessage();

            message.setText(json);
            message.setStringProperty("CPPNAME", commandName);

            messageProducer.send(message);
        } catch (JMSException e) {
            close();
            throw new RuntimeException("Failed to send message. commandName: '" + commandName + "', json: " + json, e);
        }
    }

    @Override
    public void close() {
        close(messageProducer);
        close(session);
        close(connection);

        session = null;
        messageProducer = null;
        connection = null;
    }

    private void close(final AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
