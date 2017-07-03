package com.tuz.network;

/**
 * Class that will parse http response into desired output.
 *
 * @see RequestBuilder#RequestBuilder(String, Method, ResponseParser)
 */
public interface ResponseParser<Output> {

    /**
     * Parse the response of the network request.
     *
     * @param response the response.
     * @return the parsed object.
     * @throws Exception if anything goes wrong while parsing.
     */
    Output parseResponse(Response response) throws Exception;
}
