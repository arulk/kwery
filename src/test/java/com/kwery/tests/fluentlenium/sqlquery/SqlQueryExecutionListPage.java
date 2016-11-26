package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.SqlQueryExecution.Status;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class SqlQueryExecutionListPage extends FluentPage implements RepoDashPage {
    public static final int RESULT_TABLE_COLUMN_COUNT = 3;

    @Override
    public boolean isRendered() {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until("#executionListTableBody").isDisplayed();
        return $("#executionListTableBody").first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/1/execution-list";
    }

    public List<String> getExecutionListHeaders() {
        List<String> headers = new ArrayList<>(RESULT_TABLE_COLUMN_COUNT);
        List<FluentWebElement> headerColumns = $("#executionListTable thead th");
        for (FluentWebElement headerColumn : headerColumns) {
            headers.add(headerColumn.getText());
        }

        return headers;
    }

    public List<List<String>> getExecutionList() {
        List<List<String>> resultRows = new LinkedList<>();

        List<FluentWebElement> rows = $("#executionListTableBody tr");

        for (FluentWebElement row : rows) {
            List<String> resultColumns = new ArrayList<>(RESULT_TABLE_COLUMN_COUNT);

            List<FluentWebElement> columns = row.find(By.tagName("td"));

            for (FluentWebElement column : columns) {
                resultColumns.add(column.getText());
            }

            resultRows.add(resultColumns);
        }

        return resultRows;
    }

    public String sqlQuery() {
        return find(className("f-sql-query")).getText();
    }

    public void fillStatus(Status... statuses) {
        for (Status status : statuses) {
            find(id(status.name().toLowerCase() + "CheckBox")).click();
        }
    }

    public void clickFilter() {
        $(className("f-collapse")).click();
        await().atMost(30, SECONDS).until("#collapseOne").isDisplayed();
    }

    public boolean isFilterCollapsed() {
        return !$(id("collapseOne")).first().isDisplayed();
    }

    public boolean isFilterOpen() {
        return !isFilterCollapsed();
    }

    public void fillExecutionStartStart(String date) {
        fill("#executionStartStart").with(date);
    }

    public void fillExecutionStartEnd(String date) {
        fill("#executionStartEnd").with(date);
    }

    public void fillExecutionEndStart(String date) {
        fill("#executionEndStart").with(date);
    }

    public void fillExecutionEndEnd(String date) {
        fill("#executionEndEnd").with(date);
    }

    public void fillResultCount(int resultCount) {
       fill("#resultCount").with(String.valueOf(resultCount));
    }

    public void clickPrevious() {
        find(id("previous")).click();
    }

    public void clickNext() {
        find(id("next")).click();
    }

    public void filter() {
        find(id("filterButton")).click();
    }

    public void waitForFilterResult(int expectedRowCount) {
        await().atMost(30, SECONDS).until("#executionListTableBody tr").hasSize(expectedRowCount);
    }

    public void waitForStatus(Status status) {
        await().atMost(30, SECONDS).until(".status").hasText(status.name());
    }

    public boolean isNextEnabled() {
        return !Arrays.asList(find(className("f-next")).getAttribute("class").split(" ")).contains("disabled");
    }

    public boolean isPreviousEnabled() {
        return !Arrays.asList(find(className("f-previous")).getAttribute("class").split(" ")).contains("disabled");
    }

    public String statusLink(int position) {
        return find(className("status-link")).get(position).getAttribute("href");
    }

    public boolean isStatusText(int position) {
        return find(className("status")).get(position).find(By.tagName("span")).first().isDisplayed();
    }

    public boolean isStatusLink(int position) {
        return find(className("status")).get(position).find(By.tagName("a")).first().isDisplayed();
    }
}