package fluentlenium.datasource;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import util.Messages;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static util.Messages.DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M;
import static util.Messages.DATASOURCE_DELETE_SUCCESS_M;
import static util.Messages.USER_DELETE_SUCCESS_M;
import static util.Messages.USER_DELETE_YOURSELF_M;

public class ListDatasourcesPage extends FluentPage implements RepoDashPage {
    public static final int COLUMNS = 6;

    @Override
    public boolean isRendered() {
        return find(id("datasourcesListTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#datasource/list";
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement header : $("#datasourcesListTable tr th")) {
            headers.add(header.getText());
        }

        return headers;
    }

    public List<List<String>> rows() {
        List<List<String>> rows = new LinkedList<>();

        for (FluentWebElement tr : $("#datasourcesListTableBody tr")) {
            List<String> row = new ArrayList<>(COLUMNS);
            row.addAll(tr.find(By.tagName("td")).stream().map(FluentWebElement::getText).collect(Collectors.toList()));
            rows.add(row);
        }

        return rows;
    }

    public void waitForRows(int rowCount) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#datasourcesListTableBody tr").hasSize(rowCount);
    }

    public void delete(int row) {
        FluentList<FluentWebElement> fluentWebElements = find("#datasourcesListTableBody tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("delete")).click();
    }

    public void waitForDeleteSuccessMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(format(DATASOURCE_DELETE_SUCCESS_M, label));
    }

    public void waitForDeleteFailureSqlQueryMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error p").hasText(DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M);
    }
}
