package com.kwery.tests.fluentlenium.user.save.edit;

import com.kwery.tests.fluentlenium.user.save.UserSavePage;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
public class UserEditPage extends UserSavePage {
    @Override
    public String getUrl() {
        return "/#user/{userId}";
    }
}
