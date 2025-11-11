package uk.gov.moj.cpp.boxworkmanagement.process.delegate;


import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.boxworkmanagement.event.processor.delegate.CreateBoxworkTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CreateBoxworkTaskTest {

    private static final String CASE_ID_ATTRIBUTE = "caseId";

    private final String caseId = randomUUID().toString();

    @Captor
    private ArgumentCaptor<JsonEnvelope> envelopeCaptor;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private CreateBoxworkTask createBoxworkTask;

    @BeforeEach
    public void setup() {
        when(execution.getVariable(CASE_ID_ATTRIBUTE)).thenReturn(caseId);
        }

    @Test
    public void execute() {
      createBoxworkTask.execute(execution);


    }
}