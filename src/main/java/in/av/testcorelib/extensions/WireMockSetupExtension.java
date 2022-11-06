package in.av.testcorelib.extensions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockSetupExtension extends ZephyrReferencesExtension
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    private static final int WIREMOCK_PORT = Integer.parseInt(System.getProperty("wiremock.port"));

    private final WireMockServer wireMockServer = new WireMockServer(options().port(WIREMOCK_PORT));

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        WireMock.removeAllMappings();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        WireMock.configureFor(WIREMOCK_PORT);
        wireMockServer.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
        wireMockServer.stop();
    }

}
