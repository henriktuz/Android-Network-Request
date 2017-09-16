package com.tuz.network;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by henrik on 2017-09-12.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MultipartFormUnitTest {

    private static class Updater implements ProgressUpdater {

        private long mTotal;

        private long mCurrent;

        @Override
        public void update(long total, long current) {
            mTotal = total;
            mCurrent = current;
        }
    }

    @Test
    public void testMultipartFormProgress() {
        MultipartForm form = new MultipartForm();
        form.addTextField("Hej", "Hej");
        Updater updater = new Updater();


        try (DataOutputStream os = new DataOutputStream(new ByteArrayOutputStream())) {
            form.writeTo(os, updater);

            assertEquals(updater.mCurrent, updater.mTotal);
        } catch (IOException e) {
            fail("Exception caught");
        }
    }
}
