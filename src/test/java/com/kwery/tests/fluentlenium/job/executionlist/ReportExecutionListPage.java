package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/{jobId}/execution-list/?resultCount={resultCount}")
public class ReportExecutionListPage extends KweryFluentPage implements RepoDashPage {
    protected int jobId;
    protected int resultCount;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".execution-list-container-f")).displayed();
        return true;
    }

    public void waitForRows(int rows) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".execution-list-table-f tr")).size(rows + 1); //Accommodate headers
    }

    public List<ReportExecutionListRow> executionListTable() {
        List<ReportExecutionListRow> rows = new LinkedList<>();
        for (FluentWebElement trs : $(".execution-list-table-f tbody tr")) {
            rows.add(new ReportExecutionListRow(
                            trs.find(className("execution-start-f")).text(),
                            trs.find(className("execution-end-f")).text(),
                            trs.find(className("execution-status-f")).text(),
                            trs.find(className("execution-status-f")).attribute("href")
                    )
            );
        }
        return rows;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public boolean isNextEnabled() {
        return !Arrays.asList(find(className("next-f")).attribute("class").split(" ")).contains("disabled");
    }

    public boolean isPreviousEnabled() {
        return !Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled");
    }

    public void clickPrevious() {
        $(id("previous")).click();
    }

    public void clickNext() {
        $(id("next")).click();
    }

    public void waitUntilPreviousIsEnabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> !Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled")
        );
    }

    public void waitUntilPreviousIsDisabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled")
        );
    }

    public void filterResult(String startDate, String endDate) {
        fillStart(startDate);
        fillEnd(endDate);
        clickFilter();
    }

    public void clickFilter() {
        $(".filter-submit-f").click();
    }

    public void fillEnd(String endDate) {
        $(".filter-end-f").fill().with(endDate);
    }

    public void fillStart(String startDate) {
        $(".filter-start-f").fill().with(startDate);
    }

    public void waitForExecutionListTableUpdate(String firstRowStartDate) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> executionListTable().get(0).getStart().equals(firstRowStartDate)
        );
    }

    public void waitForStartValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".start-error-f")).text(REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_START_M);
    }

    public void waitForStartValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".start-error-f")).text("");
    }

    public void waitForEndValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".end-error-f")).text(REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_END_M);
    }

    public void waitForEndValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".end-error-f")).text("");
    }

    public void removeCalendarDropDown() {
        //https://bugs.chromium.org/p/chromedriver/issues/detail?id=35
        Actions actions = new Actions(getDriver());
        actions.moveToElement(getDriver().findElement(className("execution-list-container-f")));
        actions.click();
        actions.sendKeys(Keys.ESCAPE);
        actions.build().perform();
    }

    public void deleteExecution(int index) {
        el(String.format(".execution-list-row-%d-f .delete-report-execution-f", index)).click();
    }

    public void waitForDeleteSuccessMessage() {
        waitForSuccessMessage(REPORT_JOB_EXECUTION_DELETE_M);
    }

    public void waitForRowDelete(int index) {
        waitForElementDisappearance(className(String.format(".execution-list-row-%d-f", index)));
    }
}
