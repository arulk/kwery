package com.kwery.tests.fluentlenium.job.save.update.noemailrule;

import org.junit.Test;

public class ReportUpdateNoEmailRuleTrueUiTest extends AbstractReportUpdateNoEmailRuleUiTest {
    boolean noEmail = true;

    @Test
    public void test() {
        page.waitForModalDisappearance();
        page.ensureEmailRuleChecked();
        page.submitReportSaveForm();
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();
        assertNoEmailRule(noEmail);
    }

    @Override
    public boolean isNoEmailRule() {
        return noEmail;
    }
}
