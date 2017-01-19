package com.kwery.tests.fluentlenium.joblabel.list;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.text.MessageFormat;
import java.util.List;

import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;

public class ReportLabelListPage extends KweryFluentPage implements RepoDashPage {
    protected int expectedRows;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".report-label-list-f tr").hasSize(getExpectedRows() + 1); //Taking into account header
        return true;
    }

    @Override
    public String getUrl() {
        return "/#report-label/list";
    }

    public int getExpectedRows() {
        return expectedRows;
    }

    public void setExpectedRows(int expectedRows) {
        this.expectedRows = expectedRows;
    }

    public List<String> getLabels() {
        return $(className("label-f")).stream().map(FluentWebElement::getText).collect(toList());
    }

    public void clickDelete(int index) {
        $(className(String.format("delete-%d-f", index))).click();
    }

    public List<String> errorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::getText).collect(toList());
    }

    public void waitForSuccessMessage(String label) {
        super.waitForSuccessMessage(MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_SUCCESS_M, label));
    }
}