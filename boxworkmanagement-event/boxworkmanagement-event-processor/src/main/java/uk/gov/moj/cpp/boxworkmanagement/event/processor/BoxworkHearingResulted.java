package uk.gov.moj.cpp.boxworkmanagement.event.processor;

import static java.util.UUID.randomUUID;
import static javax.json.Json.createObjectBuilder;
import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;
import static uk.gov.justice.services.messaging.Envelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.external.CourtApplications;
import uk.gov.moj.cpp.external.Hearing;
import uk.gov.moj.cpp.external.HearingResulted;
import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;
import com.google.common.base.Strings;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceComponent(EVENT_PROCESSOR)
public class BoxworkHearingResulted {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxworkHearingResulted.class);
    private static final String PUBLIC_BOXWORK_APPLICATION_COMPLETED = "public.boxwork-application-completed";
    private static final String APPLICATION_ID = "applicationId";
    private static final String BOXWORK_HEARING_RESULTED_TASK = "boxworkHearingResultedTask";
    @Inject
    @ServiceComponent(EVENT_PROCESSOR)
    private Sender sender;
    @Inject
    private RuntimeService runtimeService;

    @Handles("public.events.hearing.hearing-resulted")
    public void markBoxworkApplicationAsCompleted(final Envelope<HearingResulted> envelope) {
        LOGGER.info("Received event 'public.events.hearing.hearing-resulted' {}", envelope.payload());
        final Hearing hearing = envelope.payload().getHearing();
        if (hearing.isBoxHearing() != null && hearing.isBoxHearing()) {
            final List<CourtApplications> courtApplications = hearing.getCourtApplications();
            courtApplications.stream().forEach(appId -> nudgeApplication(appId.getId().toString()));

        } else {
            LOGGER.info("Skipping event 'public.events.hearing.hearing-resulted' as it is not boxHearing metadata {} payload {}", envelope.metadata(), envelope.payload());
        }
    }

    private void nudgeApplication(final String appId) {
        final Execution execution = runtimeService.createExecutionQuery()
                .activityId(BOXWORK_HEARING_RESULTED_TASK)
                .variableValueEquals(APPLICATION_ID, appId)
                .singleResult();
        if (execution != null && !Strings.isNullOrEmpty(execution.getId())) {
            LOGGER.info("Nudging boxworkApplication process with id {} for Application id : {}", execution.getId(), appId);
            runtimeService.signal(execution.getId());

            final JsonEnvelope boxworkApplicationCompleted = envelopeFrom(
                    metadataBuilder()
                            .withId(randomUUID())
                            .withName(PUBLIC_BOXWORK_APPLICATION_COMPLETED),
                    createObjectBuilder().add(APPLICATION_ID, appId)
                            .add("accessedAt", LocalDateTime.now().toString()).build());

            sender.send(boxworkApplicationCompleted);

        } else {
            LOGGER.info("No process in execution for Application id {}", appId);
        }
    }
}
