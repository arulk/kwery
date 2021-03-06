package com.kwery.tests.dao.jobexecutiondao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.JobExecutionModel.*;
import static com.kwery.models.JobExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class JobExecutionDaoDeleteByJobIdTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected JobModel jobModel0;
    protected JobModel jobModel1;

    @Before
    public void setUpSqlQueryExecutionDaoDeleteBySqlQueryIdTest() {
        jobModel0 = TestUtil.jobModelWithoutDependents();
        jobModel1 = TestUtil.jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel0, jobModel1));

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(JobExecutionModel.TABLE)
                                .columns(JobExecutionModel.COLUMN_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_STATUS, JobExecutionModel.JOB_ID_FK_COLUMN)
                                .values(1, 1475158740747l, 1475159940797l, "executionId", SUCCESS, jobModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, 1475158740747l, "executionId", SUCCESS, jobModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
/*        sqlQueryExecutionDao.deleteBySqlQueryId(1);
        assertTable(JobExecutionModel.TABLE, "sqlQueryExecutionDaoDeleteBySqlQueryIdTest.xml");*/
    }
}
