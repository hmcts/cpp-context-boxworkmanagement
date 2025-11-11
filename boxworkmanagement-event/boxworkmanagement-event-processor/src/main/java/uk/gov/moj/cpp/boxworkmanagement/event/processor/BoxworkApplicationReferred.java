package uk.gov.moj.cpp.boxworkmanagement.event.processor;

import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.moj.cpp.external.CourtApplications;
import uk.gov.moj.cpp.external.Hearing;
import uk.gov.moj.cpp.external.ReferToBoxwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceComponent(EVENT_PROCESSOR)
public class BoxworkApplicationReferred {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxworkApplicationReferred.class);

    private static final String APPLICATION_ID = "applicationId";
    private static final String HEARING_ID = "hearingId";
    private static final String BOXWORK_APPLICATION = "boxworkApplication";

    @Inject
    private RuntimeService runtimeService;
    private Map<String, Object> processVariables = new HashMap<>();

    @Handles("public.progression.boxwork-application-referred")
    public void processBoxworkApplicationReferred(final Envelope<ReferToBoxwork> envelope) {
        LOGGER.info("Received event 'public.progression.boxwork-application-referred' {}", envelope.payload());
        final Hearing hearing = envelope.payload().getHearing();
        if (hearing.isBoxHearing()) {
            final String hearingId = hearing.getId().toString();
            processVariables.put(HEARING_ID, hearingId);

            final List<CourtApplications> courtApplications = hearing.getCourtApplications();
            courtApplications.stream().forEach(appId -> startBoxworkApplication(appId.getId().toString()));

       } else {
            LOGGER.info("Skipping event 'public.progression.boxwork-application-referred' as it is not boxHearing metadata {} payload {}", envelope.metadata(), envelope.payload());
        }
    }
    private void startBoxworkApplication(final String appId){

        processVariables.put(APPLICATION_ID, appId);
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BOXWORK_APPLICATION, appId, processVariables);

        LOGGER.info("Boxwork Application Started for business key {}", processInstance.getBusinessKey());

    }
}
