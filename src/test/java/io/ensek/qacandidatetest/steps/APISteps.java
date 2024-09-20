package io.ensek.qacandidatetest.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.ensek.qacandidatetest.enums.Endpoints;
import io.restassured.response.ValidatableResponse;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

/**
 * Created by Hiral Yagnik
 * Project Name: ENSEK_API
 */
public class APISteps {
    static ValidatableResponse response;

    @Given("user is accessing GET Energy endpoing")
    public void userIsAccessingGETEnergyEndpoing() {

    }

    @When("user sends GET request to Engery endpoint")
    public void userSendsGETRequestToEngeryEndpoint() {

        ValidatableResponse response = given()
                .get(Endpoints.GetEnergy)
                .then().log().all();

    }

    @Then("user should get status code {int}")
    public void userShouldGetStatusCode(int statusCode) {
      response.assertThat().statusCode(statusCode);
    }

    @Then("User should get all energy data")
    public void userShouldGetAllEnergyData() {
        response.assertThat().statusCode(200);
        response.assertThat().body(hasItems("energyId", "energyType", "energyValue"));
    }
}
