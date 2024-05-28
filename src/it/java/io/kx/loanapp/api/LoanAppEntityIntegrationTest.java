package io.kx.loanapp.api;

import com.google.protobuf.Empty;
import io.kx.loanapp.Main;
import io.kx.loanapp.domain.LoanAppDomain;
import kalix.javasdk.testkit.junit.jupiter.KalixTestKitExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Kalix Runtime
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class LoanAppEntityIntegrationTest {

  /**
   * The test kit starts both the service container and the Kalix Runtime.
   */
  @RegisterExtension
  public static final KalixTestKitExtension testKit =
    new KalixTestKitExtension(Main.createKalix());

  /**
   * Use the generated gRPC client to call the service through the Kalix Runtime.
   */
  private final LoanAppService client;

  public LoanAppEntityIntegrationTest() {
    client = testKit.getGrpcClient(LoanAppService.class);
  }

  @Test
  public void testSubmit() throws Exception {
    // client.submit(LoanAppApi.SubmitCommand.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
    String uuid = UUID.randomUUID().toString();
    LoanAppApi.SubmitCommand submitCmd = LoanAppApi.SubmitCommand.newBuilder()
            .setLoanAppId(uuid)
            .setClientId("Peter-client-id-1")
            .setClientMonthlyIncomeCents(1000000)
            .setLoanAmountCents(20000000)
            .setLoanDurationMonths(45)
            .build();
    client.submit(submitCmd).toCompletableFuture().get(5, SECONDS);
    LoanAppApi.GetCommand getCmd = LoanAppApi.GetCommand.newBuilder()
            .setLoanAppId(uuid).build();
    LoanAppApi.LoanAppState getLoanAppState = client.get(getCmd).toCompletableFuture().get(5, SECONDS);
    assertEquals(LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW, getLoanAppState.getStatus());

  }

  @Test
  @Disabled("to be implemented")
  public void testGet() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.get(LoanAppApi.GetCommand.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  @Disabled("to be implemented")
  public void testApprove() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.approve(LoanAppApi.ApproveCommand.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  @Disabled("to be implemented")
  public void testDecline() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.decline(LoanAppApi.DeclineCommand.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }
}
