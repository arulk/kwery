package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobWithDependentsTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    private Datasource datasource1;

    private JobModel childJob;
    private SqlQueryModel childSqlQueryModel;
    private JobModel parentJobModel0;
    private JobModel parentJobModel1;

    JobDao jobDao;

    @Before
    public void setUpJobApiControllerUpdateJobWithDependentsTest() {
        parentJobModel0 = jobModelWithoutDependents();
        parentJobModel0.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel0);

        Datasource datasource0 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource0.setId(dbId());
        datasourceDbSetup(datasource0);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel(datasource0);
        sqlQueryModel0.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel0);

        parentJobModel0.getSqlQueries().add(sqlQueryModel0);
        jobSqlQueryDbSetUp(parentJobModel0);

        parentJobModel0.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel0);

        datasource1 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource1.setLabel("mysql0");
        datasource1.setId(dbId());
        datasourceDbSetup(datasource1);

        childJob = jobModelWithoutDependents();
        childJob.setCronExpression(null);
        jobDbSetUp(childJob);

        parentJobModel0.getChildJobs().add(childJob);
        jobDependentDbSetUp(parentJobModel0);

        childSqlQueryModel = sqlQueryModel(datasource0);
        childSqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(childSqlQueryModel);

        childJob.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJob);

        childJob.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(childJob);

        parentJobModel1 = jobModelWithoutDependents();
        parentJobModel1.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel1);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource0);
        sqlQueryModel1.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel1);

        parentJobModel1.getSqlQueries().add(sqlQueryModel1);
        jobSqlQueryDbSetUp(parentJobModel1);

        parentJobModel1.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel1);

        JobService jobService = getInjector().getInstance(JobService.class);
        jobService.schedule(parentJobModel0.getId());
        jobService.schedule(parentJobModel1.getId());

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        ImmutableSet<String> emails = ImmutableSet.of("foo@bar.com", "goo@moo.com");
        jobDto.setEmails(emails);
        jobDto.setId(childJob.getId());
        jobDto.setParentJobId(parentJobModel1.getId());

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setLabel(jobDto.getLabel());
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setParentJob(parentJobModel1);
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setId(jobDto.getId());

        expectedJobModel.setParentJob(parentJobModel1);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDto.setDatasourceId(datasource1.getId());
        sqlQueryDto.setId(childSqlQueryModel.getId());

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedSqlQueryModel.setDatasource(datasource1);
        expectedSqlQueryModel.setId(childSqlQueryModel.getId());

        expectedJobModel.setSqlQueries(ImmutableSet.of(expectedSqlQueryModel));
        parentJobModel1.setChildJobs(ImmutableSet.of(expectedJobModel));

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        assertThat(expectedJobModel, theSameBeanAs(jobDao.getJobById(jobDto.getId())));
        assertThat(jobDao.getJobById(parentJobModel1.getId()).getChildJobs().iterator().next(), theSameBeanAs(jobDao.getJobById(jobDto.getId())));
    }
}