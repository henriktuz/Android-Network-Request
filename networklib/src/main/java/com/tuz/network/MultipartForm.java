package com.tuz.network;

import android.webkit.MimeTypeMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Multipart form implementation.
 *
 * @see RequestBuilder
 */
public class MultipartForm {

    /**
     * Multipart boundary identifier.
     */
    private static final String BOUNDARY = "*****";

    /**
     * Line end identifier.
     */
    private static final byte[] LINE_END = "\r\n".getBytes(StandardCharsets.UTF_8);

    /**
     * Two hyphens identifier.
     */
    private static final String TWO_HYPHENS = "--";

    /**
     * The max buffer size.
     */
    private static final int MAX_BUFFER_SIZE = 16 * 1024;

    /**
     * Form writer base class.
     */
    static abstract class FormWriter {

        /**
         * The form boundary.
         */
        final String mBoundary;

        /**
         * The field name.
         */
        final String mFieldName;

        /**
         * The text content to write.
         */
        private final byte[] mTextContent;

        /**
         * Constructor.
         *
         * @param boundary  the boundary.
         * @param fieldName the field name.
         */
        FormWriter(String boundary, String fieldName) {
            mBoundary = boundary;
            mFieldName = fieldName;
            mTextContent = getTextContent();
        }

        /**
         * Get the size of the data to write.
         *
         * @return the size.
         */
        long size() {
            return mTextContent.length;
        }

        /**
         * Get the text content.
         *
         * @return the text part of the writer.
         */
        abstract byte[] getTextContent();

        /**
         * Write the form data.
         *
         * @param updater     the progress updater.
         * @param os          the output stream.
         * @param size        the total size to transfer.
         * @return the total number of bytes written.
         *
         * @throws IOException when writing fails.
         */
        void onWrite(ProgressUpdater updater, DataOutputStream os, long size)
                throws IOException {
            os.write(mTextContent);
            updater.update(size, os.size());
        }
    }

    /**
     * Class for writing a file to the form.
     */
    private static class TextWriter extends FormWriter {

        /**
         * The string value.
         */
        private final String mValue;

        /**
         * Constructor.
         *
         * @param boundary  the form boundary.
         * @param fieldName the field name.
         * @param value     the value.
         */
        public TextWriter(String boundary, String fieldName, String value) {
            super(boundary, fieldName);
            mValue = value;
        }

        @Override
        byte[] getTextContent() {
            StringBuilder builder = new StringBuilder(TWO_HYPHENS + mBoundary + LINE_END);
            builder.append("Content-Disposition: form-data; name\"");
            builder.append(mFieldName);
            builder.append("\"" + LINE_END);
            builder.append("Content-Type: text/plain; charset=utf-8" + LINE_END);
            builder.append(mValue);
            return builder.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * File form writer.
     */
    private static class FormFile extends FormWriter {

        /**
         * The file to upload.
         */
        private final File mFile;

        /**
         * Flag for if the mime should be added.
         */
        private final boolean mAddMime;

        /**
         * Constructor.
         *
         * @param boundary  the form boundary.
         * @param fieldName the field name.
         * @param file      the file to upload.
         * @param addMime   flag for if the mime type should be added.
         */
        FormFile(String boundary, String fieldName, File file, boolean addMime) {
            super(boundary, fieldName);
            mFile = file;
            mAddMime = addMime;
        }

        @Override
        byte[] getTextContent() {
            StringBuilder builder = new StringBuilder(TWO_HYPHENS + mBoundary + LINE_END);
            builder.append("Content-Disposition: form-data; name=\"" + mFieldName + "\"; ");
            builder.append("fileName=\"" + mFile.getName() + "\"" + LINE_END);
            builder.append("Content-Transfer-Encoding: binary" + LINE_END);

            String mime = getMime();
            if (mAddMime && mime != null) {
                builder.append("Content-Type: " + mime + LINE_END + LINE_END);
            } else {
                builder.append(LINE_END);
            }
            return builder.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        long size() {
            return super.size() + mFile.length();
        }

        @Override
        void onWrite(ProgressUpdater updater, DataOutputStream os, long size) throws IOException {
            super.onWrite(updater, os, size);

            // Write the file.
            try (InputStream is = new FileInputStream(mFile)) {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                int read;

                while ((read = is.read(buffer)) != -1) {

                    if (Thread.currentThread().isInterrupted()) {
                        os.write(buffer, 0, read);

                        // Report the current progress.
                        updater.update(size, os.size());
                    } else {
                        throw new RuntimeException("Multi part upload was interrupted.");
                    }
                }
            }
        }

        /**
         * Get the mime type from the file extension.
         *
         * @return mime type or null if no mime matches the file extension.
         */
        private String getMime() {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(mFile.getName());

            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        }
    }


    /**
     * The form fields.
     */
    private final List<FormWriter> mFormWriters;

    /**
     * The form boundary.
     */
    private final String mBoundary;

    /**
     * Constructor.
     */
    public MultipartForm() {
        this(getMultipartBoundary());
    }

    /**
     * Constructor.
     *
     * @param boundary the boundary.
     */
    public MultipartForm(String boundary) {
        mBoundary = boundary;
        mFormWriters = new ArrayList<>();
    }

    /**
     * Add a file to the form.
     *
     * @param fieldName the field name.
     * @param file      the file to add.
     * @param addMime   flag for if the mime type of the field should be added.
     */
    public void addFile(String fieldName, File file, boolean addMime) {
        mFormWriters.add(new FormFile(mBoundary, fieldName, file, addMime));
    }

    /**
     * Add text to the form.
     *
     * @param fieldName the field name.
     * @param value     the text.
     */
    public void addTextField(String fieldName, String value) {
        mFormWriters.add(new TextWriter(mBoundary, fieldName, value));
    }

    /**
     * Add text to the form.
     *
     * @param fieldName the field name.
     * @param value     the value.
     */
    public void addTextField(String fieldName, int value) {
        mFormWriters.add(new TextWriter(mBoundary, fieldName, String.valueOf(value)));
    }

    /**
     * Write form to the output stream.
     *
     * @param connection the connection to write to.
     * @param updater    the progress updater.
     * @throws IOException when writing fails.
     */
    void onWriteTo(HttpURLConnection connection, ProgressUpdater updater) throws IOException {
        connection.setRequestProperty(Http.CONTENT_TYPE, getMultipartContentType());

        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
            writeTo(os, updater);
        }
    }

    /**
     * Write to the opened output stream.
     *
     * @param os      the output stream.
     * @param updater the progress updater.
     * @throws IOException when writing fails.
     */
    void writeTo(DataOutputStream os, ProgressUpdater updater) throws IOException {
        long size = size();

        for (FormWriter writer : mFormWriters) {
            writer.onWrite(updater, os, size);
            os.write(LINE_END);
        }
        updater.update(size, os.size());
    }

    /**
     * Get the total size of all the data to write.
     *
     * @return the total size.
     */
    long size() {
        long size = 0;
        for (FormWriter writer : mFormWriters) {
            // Line end is added after each writer so add the size.
            size += (writer.size() + LINE_END.length);
        }
        return size;
    }

    /**
     * Get the boundary.
     *
     * @return the boundary.
     */
    private static String getMultipartBoundary() {
        return BOUNDARY + System.currentTimeMillis() + BOUNDARY;
    }

    /**
     * Get the multipart content type.
     *
     * @return the multipart boundary string.
     */
    private String getMultipartContentType() {
        return "multipart/form-data; boundary=" + mBoundary;
    }
}
