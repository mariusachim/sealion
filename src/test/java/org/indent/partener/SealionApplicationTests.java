package org.indent.partener;

import feign.*;
import feign.codec.StringDecoder;
import feign.form.FormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class SealionApplicationTests {

    PartnerClient client;

    PartnerLoginClient loginClient;

    interface PartnerLoginClient {
        @RequestLine("POST /ro/action/autentificare/login/")
        @Headers("Content-Type: application/x-www-form-urlencoded")
        Response login(@Param("username") String username, @Param("password") String password);
    }

    interface PartnerClient {
        @RequestLine("GET /")
        Response get();

        @RequestLine("GET /ro/cont")
        Response account();
    }

    @Before
    public void setupFeign() {
        client = Feign.builder().decoder(new StringDecoder()).target(PartnerClient.class, "http://tenispartener.ro");
        loginClient = Feign.builder().encoder(new FormEncoder()).target(PartnerLoginClient.class, "http://tenispartener.ro");
    }

    @Test
    public void testLogin() throws Exception {
        Response response = loginClient.login("forgot", "password");
        typicalInquiry(response);
    }

    @Test
    @Ignore
    public void testMain() throws Exception {
        Response response = client.get();
        typicalInquiry(response);
    }

    @Test
    @Ignore
    public void testAccount() throws Exception {
        Response response = client.get();
        typicalInquiry(response);
    }


    private void typicalInquiry(Response response) throws IOException {
        log.debug(body(response));
        log.debug(statusString(response));
        log.debug(ToStringBuilder.reflectionToString(headers(response)));
        //displayHeaders(headers(response));
        String sessionCookieValue = findFirstHeaderValue(response, "set-cookie");
        log.debug(sessionCookieValue);
        log.debug(parseSetCookieSubValues(sessionCookieValue));
    }

    private String statusString(Response httpResponse) {
        return String.valueOf(httpResponse.status());
    }

    private String body(Response httpResponse) throws IOException {
        return IOUtils.toString(httpResponse.body().asInputStream(), Charset.defaultCharset().toString());
    }

    private Map<String, Collection<String>> headers(Response httpResponse) throws IOException {
        return httpResponse.headers();
    }

    private String findFirstHeaderValue(Response httpResponse, String header) {
        Collection<String> values = httpResponse.headers().get(header);
        if (values == null) {
            throw new IllegalArgumentException("No header present in response: " + header);
        }
        log.debug("set-cookie no. entries = " + values.stream().count());
        Optional<String> first = values.stream().findFirst();
        Assert.isTrue(first.isPresent());
        return first.get();
    }

    private void displayHeaders(Map<String, Collection<String>> headers) {
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String string : entry.getValue()) {
                log.debug(key + ": " + string);
            }
        }
    }

    private void findSessionCookie(Map<String, Collection<String>> headers) {
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String string : entry.getValue()) {
                log.debug(key + ": " + string);
            }
        }
    }

    private String parseSetCookieSubValues(String cookie) {
        String[] tokens = cookie.split(";");
        Assert.notNull(tokens);
        Assert.isTrue(tokens.length > 0);
        String[] sessionPair = tokens[0].split("=");
        Assert.notNull(sessionPair);
        Assert.isTrue(sessionPair.length > 1);
        Assert.isTrue(sessionPair[0].trim().equals("_tenispartener"));
        String session = sessionPair[1];
        Assert.notNull(session);
        return session.trim();
    }

}
