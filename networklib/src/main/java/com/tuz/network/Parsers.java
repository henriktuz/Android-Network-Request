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
        return response -> Utils.toString(response.getInputStream());
    }

    /**
     * Creates a new json object parser.
     *
     * @return a parser for a json object.
     */
    public static ResponseParser<JSONObject> newJsonParser() {
        return response -> Utils.toJson(response.getInputStream());
    }
}
