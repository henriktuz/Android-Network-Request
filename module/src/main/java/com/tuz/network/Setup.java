package com.tuz.network;

import android.net.http.HttpResponseCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by henrik on 2017-04-21.
 */

public class Setup {

    /**
     * The default cache size.
     */
    public static final long DEFAULT_CACHE_SIZE = 5 * 1024L * 1024L;

    /**
     * Install the response cache.
     *
     * @param dir the directory of the cache.
     * @param size the max size of the cache.
     */
    public static void installCache(File dir, long size) throws IOException {
        HttpResponseCache.install(dir, size);
    }
}
