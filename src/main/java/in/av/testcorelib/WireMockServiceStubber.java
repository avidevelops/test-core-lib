package in.av.testcorelib;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockServiceStubber {

    private static WireMockServiceStubber wireMockServiceStubber;

    private static final int WIREMOCK_PORT = 9966;

    private MappingBuilder request;

    private ResponseDefinitionBuilder response;

    private WireMockServiceStubber() {}

    public static WireMockServiceStubber stub() {
        if (Objects.isNull(wireMockServiceStubber))
            return wireMockServiceStubber = new WireMockServiceStubber();
        else
            return wireMockServiceStubber;
    }

    public static void reset() {
        WireMock.reset();
    }

    public WireMockServiceStubber getRequest(String url) {
        request = get(urlEqualTo(url));
        response = aResponse().withHeader("Accept", "application/json")
                .withHeader("Content-type", "application/json");
        return this;
    }

    public WireMockServiceStubber getRequestWithParam(String url) {
        request = get(urlPathEqualTo(url));
        response = aResponse().withHeader("Accept", "application/json")
                .withHeader("Content-type", "application/json");
        return this;
    }

    public WireMockServiceStubber postRequest(String url) {
        request = post(urlEqualTo(url));
        response = aResponse().withHeader("Accept", "application/json")
                .withHeader("Content-type", "application/json");
        return this;
    }

    public WireMockServiceStubber putRequest(String url) {
        request = put(urlEqualTo(url));
        response = aResponse().withHeader("Accept", "application/json")
                .withHeader("Content-type", "application/json");
        return this;
    }

    public WireMockServiceStubber deleteRequest(String url) {
        request = delete(urlEqualTo(url));
        response = aResponse().withHeader("Accept", "application/json")
                .withHeader("Content-type", "application/json");
        return this;
    }

    public WireMockServiceStubber withBody(String body) {
        stubFor(request.willReturn(response.withBody(body)));
        return this;
    }

    public WireMockServiceStubber withBodyFile(String bodyFileName) {
        stubFor(request.willReturn(response.withBodyFile(bodyFileName)));
        return this;
    }

    public WireMockServiceStubber withStatus(int status) {
        stubFor(request.willReturn(response.withStatus(status)));
        return this;
    }

    public WireMockServiceStubber requestBody(String body) {
        request.withRequestBody(matching(body));
        return this;
    }

    public WireMockServiceStubber queryParam(String name, String value) {
        request.withQueryParam(name, containing(value));
        return this;
    }

    public WireMockServiceStubber queryParams(Map<String, String> qs) {
        Map<String, StringValuePattern> q = new HashMap<>();
        for (Map.Entry<String, String> entry : qs.entrySet()) {
            q.put(entry.getKey(), containing(entry.getValue()));
        }
        request.withQueryParams(q);
        return this;
    }
}
