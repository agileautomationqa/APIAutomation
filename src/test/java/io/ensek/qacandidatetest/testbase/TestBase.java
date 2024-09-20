package io.ensek.qacandidatetest.testbase;

import io.ensek.qacandidatetest.utils.PropertyReader;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

/**
 * Created by Hiral Yagnik
 * Project Name: ENSEK_API
 */

/*This is the base class for all the test classes. This class will have the common methods and variables which will be used by all the test classes.
 * This class will have the common methods like inIt() which will be used to set the base URI, base path, and other common methods.
 * This class will be extended by all the test classes.
 * */
public class TestBase {
    public static PropertyReader propertyReader;

    @BeforeClass(alwaysRun = true)  // This method will run before any test class runs
    public static void inIt() {
        propertyReader = PropertyReader.getInstance();
        RestAssured.baseURI = propertyReader.getProperty("ApiBaseUrl");
        RestAssured.basePath = "/ENSEK/";
    }


}
