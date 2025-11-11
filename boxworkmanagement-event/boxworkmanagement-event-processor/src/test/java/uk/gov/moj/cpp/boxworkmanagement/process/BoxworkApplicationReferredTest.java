package uk.gov.moj.cpp.boxworkmanagement.process;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.messaging.Envelope;
import uk.gov.moj.cpp.boxworkmanagement.event.processor.BoxworkApplicationReferred;
import uk.gov.moj.cpp.external.CourtApplications;
import uk.gov.moj.cpp.external.Hearing;
import uk.gov.moj.cpp.external.ReferToBoxwork;

import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoxworkApplicationReferredTest {

    private static final String APPLICATION_ID = "applicationId";
    private static final String HEARING_ID = "hearingId";
    private static final String BOXWORK_APPLICATION = "boxworkApplication";
    @Mock
    ReferToBoxwork referToBoxwork;
    @Mock
    Envelope<ReferToBoxwork> envelope;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RuntimeService runtimeService;
    @Captor
    private ArgumentCaptor<Map<String, Object>> processVariablesCaptor;
    @InjectMocks
    private BoxworkApplicationReferred boxworkApplicationReferred;
    private UUID hearingId = randomUUID();
    @Mock
    private Hearing hearing;
    @Mock
    private CourtApplications courtApplication1;
    private UUID APP_ID = randomUUID();

    @BeforeEach
    public void setup() {
        when(envelope.payload()).thenReturn(referToBoxwork);
        when(referToBoxwork.getHearing()).thenReturn(hearing);
        when(hearing.getId()).thenReturn(hearingId);
        when(hearing.isBoxHearing()).thenReturn(true);
        when(courtApplication1.getId()).thenReturn(APP_ID);
        when(hearing.getCourtApplications()).thenReturn(asList(courtApplication1));

    }

    @Test
    public void shouldStartBoxworkApplicationWhenEventReceived() {
        when(envelope.payload()).thenReturn(referToBoxwork);

        boxworkApplicationReferred.processBoxworkApplicationReferred(envelope);
        verify(runtimeService).startProcessInstanceByKey(eq(BOXWORK_APPLICATION), eq(APP_ID.toString()), processVariablesCaptor.capture());

        final Map<String, Object> actualProcessVariables = processVariablesCaptor.getValue();
        assertThat(actualProcessVariables.get(APPLICATION_ID), is(APP_ID.toString()));
        assertThat(actualProcessVariables.get(HEARING_ID), is(hearingId.toString()));
    }

    @AfterEach
    public void tearDown() {
        Mocks.reset();
    }

}