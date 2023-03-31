package testsimulation;

import io.gatling.core.scenario.Scenario;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class GatlingTestDemo extends Simulation {
    // 3 different sections

    // 1: Http Configuration
    private HttpProtocolBuilder httpProtocolBuilder = http
            .baseUrl("https://reqres.in/api")
            .acceptHeader("application/json");
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS","4"));
    private static final int RAMP_USERS = Integer.parseInt(System.getProperty("RAMP","10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("DURATION","30"));
    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/testdata.json").random();

    @Override
    public void before() {
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_USERS);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    private static ChainBuilder authenticate =
                        exec(http("Authenticate User").post("/login")
                                .body(ElFileBody("template/login.json")).asJson()
                    .check(status().is(200))
                       .check(jmesPath("token").saveAs("authtoken")));

    private static ChainBuilder createuser =
            feed(jsonFeeder).exec(http("Create a new User-#{first_name}").post("/users")
            .header("Autherization","#{authtoken}")
                    .body(ElFileBody("template/template.json")).asJson());

    private static ChainBuilder updateuser =
            exec(http("update user -#{id}").put("/users/#{id}").body(StringBody("{\"job\": \"zion resident\"}"))
                    .check(status().is(200)));

    // 2: scenario description
    private ScenarioBuilder scn = scenario("User details- simulation")
            .exec(authenticate)
            .pause(2)
            .exec(createuser)
            .pause(2)
            .exec(updateuser);

    // 3: load simulation
    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_USERS)
                ).protocols(httpProtocolBuilder)
        ).maxDuration(TEST_DURATION);
    }
    @Override
    public void after() {
        System.out.println("Stress test completed");
    }


}
