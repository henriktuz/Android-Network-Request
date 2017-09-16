package com.tuz.network;

import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ParserUnitTest {

    private static final String FAKE_SERVER_ROOT = "https://jsonplaceholder.typicode.com";

    @Test
    public void testBuildJsonRequest() throws Exception {
        RequestBuilder<JSONObject> builder = new RequestBuilder<>(FAKE_SERVER_ROOT + "/posts/1", Method.GET,
                Parsers.newJsonParser());
        Request<JSONObject> request = builder.build();

        JSONObject json  = request.execute();
        assertNotNull(json);
    }

    @Test
    public void testBuildStringrequest() throws Exception {
        RequestBuilder<String> builder =  new RequestBuilder<>(FAKE_SERVER_ROOT + "/posts/1",
                Method.GET, Parsers.newStringParser());

        Request<String> request = builder.build();
        assertNotNull(request.execute());
    }
}