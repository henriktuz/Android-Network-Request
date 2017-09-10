package com.tuz.network;

import org.json.JSONObject;

/**
 * Common parsers for the response.
 */
public class Parsers {

    /**
     * Creates a new string parser.
     *
     * @return a parser for strings.
     */
    public static ResponseParser<String> newStringParser() {
        return new ResponseParser<String>() {

            @Override
            public String parseResponse(Response response) throws Exception {
                return Utils.toString(response.getInputStream());
            }
        };
    }

    /**
     * Creates a new json object parser.
     *
     * @return a parser for a json object.
     */
    public static ResponseParser<JSONObject> newJsonParser() {
        return new ResponseParser<JSONObject>() {

            @Override
            public JSONObject parseResponse(Response response) throws Exception {
                return Utils.toJson(response.getInputStream());
            }
        };
    }
}
