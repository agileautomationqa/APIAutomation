package io.ensek.qacandidatetest.api;

import io.ensek.qacandidatetest.testbase.TestBase;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by Hiral Yagnik
 * Project Name: ENSEK_API
 */

// This class is used to build the request and response specifications
public class SpecBuilder extends TestBase {
    private static RequestSpecification requestSpecification;
    private static RequestSpecBuilder requestSpecBuilder;
    private static ResponseSpecification resSpecification;
    private static ResponseSpecBuilder resSpecBuilder;

    public static RequestSpecification postLoginRequestSpec() {
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(propertyReader.getProperty("ApiBaseUrl"))
                .addHeader("Content-Type", "application/json") //setting the headers
                .addHeader("accept", "application/json")
                .addFilter(new io.restassured.filter.log.RequestLoggingFilter())
                .addFilter(new io.restassured.filter.log.ResponseLoggingFilter());
        requestSpecification = requestSpecBuilder.build();
        return requestSpecification;
    }
    public static ResponseSpecification postResponseSpec() {
        resSpecBuilder = new ResponseSpecBuilder();
        resSpecBuilder.expectStatusCode(200).expectBody(containsString("Success"))
                .log(LogDetail.ALL);
        resSpecification = resSpecBuilder.build();
        return resSpecification;
    }
    public static ResponseSpecification buyEnergySpec() {
        resSpecBuilder = new ResponseSpecBuilder();
        resSpecBuilder.expectStatusCode(200).expectBody(containsString("message"))
                .log(LogDetail.ALL);
        resSpecification = resSpecBuilder.build();
        return resSpecification;
    }
    public String getOrderId(String message){
        String regex = "s*id\\s*is\\s*([a-f0-9\\-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);  // Extract the order id from the group
        }
        else return "No order id found in the response message.";
    }
}
