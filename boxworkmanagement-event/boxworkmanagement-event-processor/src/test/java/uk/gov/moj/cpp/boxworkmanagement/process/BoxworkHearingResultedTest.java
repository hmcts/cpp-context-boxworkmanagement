package uk.gov.moj.cpp.boxworkmanagement.process;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.moj.cpp.boxworkmanagement.event.processor.BoxworkHearingResulted;
import uk.gov.moj.cpp.external.CourtApplications;
import uk.gov.moj.cpp.external.Hearing;
import uk.gov.moj.cpp.external.HearingResulted;

import java.util.UUID;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoxworkHearingResultedTest {

    private UUID APP_ID = randomUUID();
    private static final String APPLICATION_ID = "applicationId";
    private static final String BOXWORK_HEARING_RESULTED_TASK = "boxworkHearingResultedTask";

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private BoxworkHearingResulted boxworkHearingResulted;
    @Mock
    private ExecutionQuery queryObject;
    @Mock
    private Execution execution;
    @Mock
    Envelope<HearingResulted> envelope;
    @Mock
    private Hearing hearing;
    @Mock
    private HearingResulted hearingResulted;
    @Mock
    private CourtApplications courtApplication1;

    @Mock
    private Sender sender;

    @BeforeEach
    public void setup() {
        when(envelope.payload()).thenReturn(hearingResulted);
        when(hearingResulted.getHearing()).thenReturn(hearing);
        when(hearing.isBoxHearing()).thenReturn(true);
        when(courtApplication1.getId()).thenReturn(APP_ID);
        when(hearing.getCourtApplications()).thenReturn(asList(courtApplication1));
        when(runtimeService.createExecutionQuery()).thenReturn(queryObject);
        when(queryObject.activityId(BOXWORK_HEARING_RESULTED_TASK)).thenReturn(queryObject);
        when(queryObject.variableValueEquals(APPLICATION_ID, APP_ID.toString())).thenReturn(queryObject);
        when(queryObject.singleResult()).thenReturn(execution);
        when(execution.getId()).thenReturn(randomUUID().toString());
    }

    @Test
    public void shouldCompleteBoxworkApplicationWhenEventReceived() {

        boxworkHearingResulted.markBoxworkApplicationAsCompleted(envelope);

        verify(queryObject).variableValueEquals(APPLICATION_ID, APP_ID.toString());
        verify(queryObject).activityId(BOXWORK_HEARING_RESULTED_TASK);
    }

    @AfterEach
    public void tearDown() {
        Mocks.reset();
    }

}