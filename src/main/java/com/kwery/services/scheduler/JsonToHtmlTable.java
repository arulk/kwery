package com.kwery.services.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JsonToHtmlTable {
    public String convert(String json) throws IOException {
        //TODO - Is there a better way to do this, probably use some XML processing library or is it an overkill?
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<List<String>>> typeReference = new TypeReference<List<List<String>>>() {};
        List<List<String>> table = objectMapper.readValue(json, typeReference);

        List<String> htmlTableParts = new LinkedList<>();
        htmlTableParts.add("<table style='border: 1px solid black; width: 100%;'>");

        //Has headers?
        if (!table.isEmpty()) {
            List<String> headers = table.get(0);

            List<String> ths = new ArrayList<>(headers.size());

            htmlTableParts.add("<tr style='border: 1px solid black;'>");

            for (String header : headers) {
                String htmlHeader = "<th style='border: 1px solid black;'>" + header + "</th>";
                ths.add(htmlHeader);
            }

            htmlTableParts.add(Joiner.on("").join(ths));

            htmlTableParts.add("</tr>");

            //Has data?

            if (table.size() > 1) {
                //Skip headers
                for (int i = 1; i < table.size(); ++i) {
                    htmlTableParts.add("<tr style='border: 1px solid black;'>");

                    List<String> row = table.get(i);
                    List<String> tds = new ArrayList<>(row.size());

                    for (String s : row) {
                        tds.add("<td style='border: 1px solid black;'>" + s + "</td>");
                    }
                    htmlTableParts.add(Joiner.on("").join(tds));

                    htmlTableParts.add("</tr>");
                }
            }
        }

        htmlTableParts.add("</table>");

        return Joiner.on("").join(htmlTableParts);
    }
}