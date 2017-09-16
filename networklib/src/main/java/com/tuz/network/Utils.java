package com.tuz.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Network utilities.
 */
public abstract class Utils {

    /**
     * The buffer size.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * End of file marker.
     */
    private static final int EOF = -1;

    /**
     * Constructor.
     */
    private Utils() {
        // No init.
    }

    /**
     * Try close a closeable.
     *
     * @param closeable the closeable.
     */
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {

        }
    }

    /**
     * Reads an input stream into an byte array.
     *
     * @param is the input stream.
     * @return the byte array.
     * @throws IOException when writing fails.
     */
    public static byte[] toByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            write(is, baos);
            return baos.toByteArray();
        }
    }

    /**
     * Reads an input stream into a string.
     *
     * @param is the input stream.
     * @return the string.
     * @throws IOException when writing fails.
     */
    public static String toString(InputStream is) throws IOException {
        return new String(toByteArray(is), StandardCharsets.UTF_8);
    }

    /**
     * Reads an input stream into a json object.
     *
     * @param is the input stream.
     * @return the json object.
     * @throws IOException   when writing fails.
     * @throws JSONException if the json object cannot be created.
     */
    public static JSONObject toJson(InputStream is) throws IOException, JSONException {
        return new JSONObject(toString(is));
    }

    /**
     * Print a json object from a stream.
     *
     * @param tag         the tag of the print.
     * @param is          the input stream.
     * @param indentation the indentation of the json string.
     * @throws IOException   when writing fails.
     * @throws JSONException if the json object cannot be created.
     */
    public static void debugPrintJson(String tag, InputStream is, int indentation) throws IOException,
            JSONException {
        Log.d(tag, toJson(is).toString(indentation));
    }

    /**
     * Copy an input stream into another input stream.
     *
     * @param is the input stream.
     * @return the copied stream.
     * @throws IOException when writing fails.
     */
    public static InputStream copy(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(is, baos);
        close(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Write an input stream to an output stream.
     *
     * @param is the input stream.
     * @param os the output stream.
     * @throws IOException when writing fails.
     */
    public static void write(InputStream is, OutputStream os) throws IOException {
        int read = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        while (read != EOF) {
            read = is.read(buffer, 0, BUFFER_SIZE);

            if (read != EOF) {
                os.write(buffer, 0, read);
            }
        }
    }

    /**
     * Save a file to disc.
     *
     * @param file the file.
     * @param is   the input stream.
     * @throws IOException when writing fails.
     */
    public static void saveToDisc(File file, InputStream is) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(is, fos);
        }
    }
}
