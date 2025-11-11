package uk.gov.moj.cpp.boxworkmanagement.healthchecks;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.moj.cpp.boxworkmanagement.healthchecks.WorkManagementJdbcDataSourceProvider.DATA_SOURCE_NAME;

import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


@ExtendWith(MockitoExtension.class)
public class WorkManagementJdbcDataSourceProviderTest {

    @Mock
    private InitialContext initialContext;

    @InjectMocks
    private WorkManagementJdbcDataSourceProvider workManagementJdbcDataSourceProvider;

    @Test
    public void shouldGetCamundaDataSourceFromInitialContext() throws Exception {

        final DataSource dataSource = mock(DataSource.class);
        when(initialContext.lookup(DATA_SOURCE_NAME)).thenReturn(dataSource);

        assertThat(workManagementJdbcDataSourceProvider.getDataSource(), is(dataSource));
    }

    @Test
    public void shouldFailIfLookupFails() throws Exception {

        final NamingException namingException = new NamingException("Ooops");
        when(initialContext.lookup(DATA_SOURCE_NAME)).thenThrow(namingException);

        final JdbcRepositoryException jdbcRepositoryException = assertThrows(
                JdbcRepositoryException.class,
                () -> workManagementJdbcDataSourceProvider.getDataSource());

        assertThat(jdbcRepositoryException.getCause(), is(namingException));
        assertThat(jdbcRepositoryException.getMessage(), is("Failed to lookup workmanagement DataSource using JNDI name 'java:/DS.workmanagement'"));

    }
}