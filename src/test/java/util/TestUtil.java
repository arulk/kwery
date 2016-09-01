package util;

import com.google.common.collect.ImmutableMap;
import models.Datasource;
import models.QueryRun;
import models.User;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static models.Datasource.Type.MYSQL;

public class TestUtil {
    protected static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    public static final String USER_NAME_COOKIE = "3067e0b13d45acae3719c25f6bccfac007bfa8cf-___TS=1471772214856&username=fo";
    public static final String COOKIE_STRING = String.format("NINJA_SESSION=%s", USER_NAME_COOKIE);

    public static User user() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        return user;
    }

    public static Map<String, String> userParams() {
        return ImmutableMap.of(
                "username", "foo",
                "password", "password"
        );
    }

    public static Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setUrl("0.0.0.0");
        datasource.setPort(3306);
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setLabel("label");
        datasource.setType(MYSQL);
        return datasource;
    }

    public static QueryRun queryRun() {
        QueryRun q = new QueryRun();
        q.setQuery("select * from foo");
        q.setLabel("test query run");
        q.setCronExpression("* *");
        return q;
    }

    public static Cookie sessionCookie(String value) {
        return new Cookie("NINJA_SESSION", value);
    }

    public static Cookie usernameCookie() {
        return sessionCookie(USER_NAME_COOKIE);
    }

    public static boolean waitForMysql(String host, int port) {
        long start = System.currentTimeMillis();
        do {
            try (Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d", host, port), "root", "root")) {
                return true;
            } catch (SQLException e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e1) {
                }
            }
        } while (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) < 2);

        return false;
    }
}