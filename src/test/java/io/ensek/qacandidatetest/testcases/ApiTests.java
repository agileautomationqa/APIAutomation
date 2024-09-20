package io.ensek.qacandidatetest.testcases;

import io.ensek.qacandidatetest.api.SpecBuilder;
import io.ensek.qacandidatetest.enums.Endpoints;
import io.ensek.qacandidatetest.model.AuthPojo;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Created by Hiral Yagnik
 * Project Name: ENSEK_API
 */
public class ApiTests extends SpecBuilder {
    static String username = "test";
    static String password = "testing";
    static String bearerToken;
    static String newOrderId;
    static String existingOrderId;
    // Get the current date in GMT
    static ValidatableResponse resp;
    ZonedDateTime today = ZonedDateTime.now(ZoneId.of("GMT"));
    LocalDate currentDate = today.toLocalDate();
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    @Test(description = "This test case will get all the orders")
    public void test001ResetAllOrders() {
        Response response = given()
                .get(Endpoints.Reset); //sending the get request
        response.then().log().all().statusCode(405); //without bearer token, you will get 405
    }

    @Test(description = "This Test will Login User and generate Token", groups = {"smoke"})
    public void test002LoginUser() {
        AuthPojo authPojo = new AuthPojo(); //creating an object of AuthPojo class
        authPojo.setUsername(username);
        authPojo.setPassword(password); //setting the username and password
        Response response = given().spec(postLoginRequestSpec())
                .when().body(authPojo).post(Endpoints.Login); //sending the post request
        response.then().assertThat() //asserting the response
                .statusCode(200) //checking if the status code is 200
                .body(containsString("access_token")) //checking if the response contains the access token
                .body("access_token", notNullValue()); //checking if the response contains the access token
        bearerToken = response.then().spec(postResponseSpec()).extract().path("access_token"); //extracting the bearer token from the response
    }

    @Test(description = "With Acceess Token - Reset Data", groups = {"smoke"})
    public void test003ResetData() {
        System.out.println("Bearer Token: " + bearerToken);
        Response response = given()
                .auth().oauth2(bearerToken)
                .when().post(Endpoints.Reset);
        response.then().spec(postResponseSpec());
    }

    @Test(description = "Get Energy", groups = {"smoke"})
    public void test004GetEnergy() {
        Response response = given()
                .get(Endpoints.GetEnergy);
        response.then().log().all().statusCode(200);

    }

    @Test(description = "Get Existing Orders", groups = {"smoke"})
    public void test005GetExistingOrders() {

        ValidatableResponse resp = given()
                .get(Endpoints.GetOrders).then().log().all().statusCode(200);
        int existingOrderSize = resp.extract().path("size()");
        System.out.println("Existing Orders: " + existingOrderSize);

         existingOrderId = resp.extract().path("id[0]");
        System.out.println("Order ID: " + existingOrderId);
    }
    @Test(description = "Get order By id", groups = {"smoke"})
    public void test006GetOrderById() {
        Response response = given()
                .get(Endpoints.GetOrderById, existingOrderId);
        response.then().log().all().statusCode(200);
    }

    @Test(description = "Buy Energy", groups = {"smoke"}, dataProvider = "fuelData")
    public void test007BuyEnergyOfEachFuelType(int energyid, int quantity) {
        Response response = given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .auth().oauth2(bearerToken)
                .when().put(Endpoints.BuyEnergy, energyid, quantity);
        response.then().spec(buyEnergySpec());
        String orderMessage = response.then().extract().path("message");
        newOrderId = getOrderId(orderMessage);
    }

    // DataProvider method for "testBuyFuel" test
    @DataProvider(name = "fuelData")
    public Object[][] guelData() {
        return new Object[][]{
                {1, 100},
                {2, 50},
                {3, 8},
                {4, 1000}
        };
    }

    @Test(description = "Get Updated Orders", groups = {"smoke"})
    public void test008GetUpdatedOrders() {
        Response response = given()
                .get(Endpoints.GetOrders);
        response.then().log().all().statusCode(200);
        // List to store filtered orders
        List<Map<String, Object>> orders = response.jsonPath().getList("");

        List<Map<String, Object>> orderedPlacedToday = new ArrayList<>();

        for (Map<String, Object> order : orders) {
            String timeString = (String) order.get("time");
            ZonedDateTime orderTime = ZonedDateTime.parse(timeString, formatter);
            LocalDate orderDate = orderTime.toLocalDate();

            // Check if the order was placed e today and add to filteredOrders
            if (orderDate.isEqual(currentDate)) {
                orderedPlacedToday.add(order);
            }
            int totalNumberOfOrders = orderedPlacedToday.size();
            System.out.println("Total Number of Orders Placed Today: " + totalNumberOfOrders);
        }
    }

    @Test(description = "Get Orders Placed Before Today", groups = {"smoke"})
    public void test009GetOrdersPlacedBeforeToday() {
        Response response = given()
                .get(Endpoints.GetOrders);
        response.then().log().all().statusCode(200);
        // List to store filtered orders
        List<Map<String, Object>> orders = response.jsonPath().getList("");

        // List to store filtered orders
        List<Map<String, Object>> filteredOrders = new ArrayList<>();

        for (Map<String, Object> order : orders) {
            String timeString = (String) order.get("time");
            ZonedDateTime orderTime = ZonedDateTime.parse(timeString, formatter);

            // Check if the order was placed before today and add to filteredOrders
            if (orderTime.isBefore(today)) {
                filteredOrders.add(order);
            }
        }
        // Number of orders placed before today
        System.out.println("Number of Orders placed before today: " + filteredOrders.size());
    }

    @Test(description = "Get Order By Id", groups = {"smoke"})
public void test010GetOrderById() {
        Response response = given()
                .get(Endpoints.GetOrderById, existingOrderId);
        response.then().log().all().statusCode(200);
    }
    @Test(description = "Delete Order By Id", groups = {"smoke"})

    public void test011DeleteOrderById() {
        Response response = given()
                .auth().oauth2(bearerToken)
                .when().delete(Endpoints.GetOrderById, newOrderId);
        response.then().log().all().statusCode(200);
    }
}


