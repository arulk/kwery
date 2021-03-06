package com.kwery.tests.services.job.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailHtmlTestUtil {
    public static void assertReportFooter(String html) {
        Document doc = Jsoup.parse(html);
        String text = doc.select(".footer-t").text();
        assertThat(text, is("Report generated by Kwery"));

        Element kweryLink = doc.select(".kwery-link-t").get(0);

        assertThat(kweryLink.text(), is("Kwery"));
        assertThat(kweryLink.attr("href"), is("http://getkwery.com/"));
    }
}
