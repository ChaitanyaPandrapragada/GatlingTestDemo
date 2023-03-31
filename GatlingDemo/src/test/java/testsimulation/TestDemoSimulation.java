package testsimulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class TestDemoSimulation extends Simulation {

private HttpProtocolBuilder httpConfiguration = http.baseUrl("https://jsonplaceholder.typicode.com/")
        .acceptHeader("application/json");

//Scenarios
    private ScenarioBuilder scn = scenario("Get all users data")
        .exec(http("get user list")
                .get("/users")
                .check(status().is(200)));

// setup
{
    setUp(scn.injectOpen(atOnceUsers(100))).protocols(httpConfiguration);


}

}
