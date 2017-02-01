package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage.SqlQueryFormField;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.INPUT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField.cronExpression;
import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField.parentReportId;
import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.SELECT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.SqlQueryFormField.datasourceId;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveValidationUiTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    ReportSavePage page;

    @Before
    public void setUpReportSaveValidationUiTest() {
        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        goTo(page);

        if (!page.isRendered()) {
            fail("Save report page could not be rendered");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void testWithCronExpressionChosen() {
        page.chooseCronExpression();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.waitForReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == parentReportId) {
                assertThat(page.validationMessage(field), is(""));
            } else {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }

        sqlQueryFormValidation();
    }

    @Test
    public void testWithCronUiChosen() {
        page.chooseCronUi();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.waitForReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == parentReportId || field == cronExpression) {
                assertThat(page.validationMessage(field), is(""));
            } else {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }

        sqlQueryFormValidation();
    }

    @Test
    public void testWithParentReportChosen() {
        page.chooseParentReport();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.waitForReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == parentReportId) {
                assertThat(page.validationMessage(field), is(SELECT_VALIDATION_ERROR_MESSAGE));
            } else if (field == cronExpression) {
                assertThat(page.validationMessage(field), is(""));
            } else {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }

        sqlQueryFormValidation();
    }

    private void sqlQueryFormValidation() {
        for (int i = 0; i < 2; ++i) {
            for (SqlQueryFormField field : SqlQueryFormField.values()) {
                if (field == datasourceId) {
                    assertThat(page.validationMessage(field, i), is(SELECT_VALIDATION_ERROR_MESSAGE));
                } else {
                    assertThat(page.validationMessage(field, i), is(INPUT_VALIDATION_ERROR_MESSAGE));
                }
            }
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
