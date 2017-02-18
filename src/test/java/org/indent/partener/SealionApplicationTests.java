package org.indent.partener;

import feign.Feign;
import feign.RequestLine;
import feign.Response;
import feign.codec.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class SealionApplicationTests {

    @Before
    public void setup() {
    }

    interface What {
        @RequestLine("GET /")
        Response get();

    }

    @Test
    public void testGoogle() throws Exception {
        What f = Feign.builder().decoder(new StringDecoder()).target(What.class, "http://tenispartener.ro");
        Response response = f.get();
        log.debug(body(response));
        log.debug(statusString(response));
        log.debug(ToStringBuilder.reflectionToString(headers(response)));
        displayHeaders(headers(response));
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

    private Collection<String> findHeaderValues(Map<String, Collection<String>> headers, String header) {
        Collection<String> values = headers.get(header);
        if (values == null) {
            throw new IllegalArgumentException("No header present in response: " + header);
        }
        return values;
    }

    private void displayHeaders(Map<String, Collection<String>> headers) {
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String string : entry.getValue()) {
                log.debug(key + ": " + string);
            }
        }
    }

}
