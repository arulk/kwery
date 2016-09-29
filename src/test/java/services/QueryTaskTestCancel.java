package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import services.scheduler.PreparedStatementExecutor;
import services.scheduler.PreparedStatementExecutorFactory;
import services.scheduler.QueryTask;
import services.scheduler.ResultSetProcessor;
import services.scheduler.ResultSetProcessorFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryTaskTestCancel {
    @Mock
    private QueryRunExecutionDao queryRunExecutionDao;
    @Mock
    private RepoDashUtil repoDashUtil;
    @Mock
    private QueryRun queryRun;
    @Mock
    private Datasource datasource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSetProcessorFactory resultSetProcessorFactory;
    @Mock
    private ResultSetProcessor resultSetProcessor;
    @Mock
    private TaskExecutionContext context;
    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private PreparedStatementExecutorFactory preparedStatementExecutorFactory;
    @Mock
    private PreparedStatementExecutor preparedStatementExecutor;

    private String executionResult = "{}";

    private QueryRunExecution queryRunExecution = new QueryRunExecution();

    private volatile boolean cancelled = false;

    @Before
    public void setUpQueryTaskTestCancel() throws SQLException, JsonProcessingException {
        when(queryRun.getDatasource()).thenReturn(datasource);
        when(queryRun.getQuery()).thenReturn("");
        when(datasource.getLabel()).thenReturn("");
        when(repoDashUtil.connection(datasource)).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(preparedStatementExecutorFactory.create(preparedStatement)).thenReturn(preparedStatementExecutor);

        when(preparedStatementExecutor.execute()).thenAnswer(new Answer<Future<ResultSet>>() {
            @Override
            public Future<ResultSet> answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.DAYS.sleep(1);
                return mock(FutureTask.class);
            }
        });

        doNothing().when(preparedStatement).cancel();


/*        when(resultSetProcessorFactory.create(resultSet)).thenReturn(resultSetProcessor);
        when(resultSetProcessor.process()).thenReturn(executionResult);
        when(context.getTaskExecutor()).thenReturn(taskExecutor);
        when(taskExecutor.getGuid()).thenReturn("foo");
        when(queryRunExecutionDao.getByExecutionId("foo")).thenReturn(queryRunExecution);
        doNothing().when(queryRunExecutionDao).update(any());*/
    }

    @Test
    public void test() throws InterruptedException {
        QueryTask queryTask = new QueryTask(queryRunExecutionDao, repoDashUtil, preparedStatementExecutorFactory, resultSetProcessorFactory, queryRun);
        Thread taskExecutionThread = new Thread() {
            @Override
            public void run() {
                queryTask.execute(context);
            }
        };
        taskExecutionThread.start();
        TimeUnit.SECONDS.sleep(10);
        taskExecutionThread.interrupt();
        assertThat(queryRunExecution.getResult(), nullValue());
    }
}
