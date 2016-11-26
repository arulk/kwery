package com.kwery.tests.fluentlenium.user;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.User;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserUpdateUiTest extends RepoDashFluentLeniumTest {
    protected UserTableUtil userTableUtil;
    protected User user;
    protected UserUpdatePage page;

    @Before
    public void setUpUpdateUserPageTest() {
        userTableUtil = new UserTableUtil(1);

        new DbSetup(
            new DataSourceDestination(getDatasource()),
                sequenceOf(
                    userTableUtil.insertOperation()
                )
        ).launch();

        UserLoginPage loginPage = createPage(UserLoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);

        user = userTableUtil.row(0);

        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user);

        if (!loginPage.isRendered()) {
            failed("Could not render login page");
        }

        page = createPage(UserUpdatePage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            failed("Could not render update user page");
        }
    }

    @Test
    public void test() {
        page.waitForUsername(user.getUsername());
        assertThat(page.isUsernameDisabled(), is(true));
        page.updateForm("foo");
        page.waitForSuccessMessage(user.getUsername());
    }
}