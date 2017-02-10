package com.kwery.tests.services.job.email.reportfailurealert;

import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.services.job.ReportFailureAlertEmailSender;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.WiserRule;
import org.junit.Before;
import org.junit.Rule;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class ReportFailureAlertEmailSenderAbstractTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    ReportFailureAlertEmailSender emailSender;
    JobExecutionModel jobExecutionModel;

    @Before
    public void setUp() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.setFailureAlertEmails(ImmutableSet.of("foo@bar.com"));
        jobFailureAlertEmailDbSetUp(jobModel);

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016
        jobExecutionModel.setStatus(JobExecutionModel.Status.FAILURE);

        jobExecutionDbSetUp(jobExecutionModel);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());

        emailSender = getInstance(ReportFailureAlertEmailSender.class);
    }

    public String expectedSubject() {
        return REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M + " - " + jobExecutionModel.getJobModel().getTitle() + " - Thu Dec 22 2016 21:29";
    }
}
