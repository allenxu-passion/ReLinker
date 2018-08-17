package com.yixia.liblink;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import android.text.TextUtils;
import android.content.Context;

public class YZBLinkerInstance {

    private static final String LIB_DIR = "yzblibs";//cache dir
    protected final YZBLinker.LinkerLoader linkerLoader;
    protected final YZBLinker.LinkerDownloader linkerDownloader;
    protected static final Set<String> loadedLibraries = new HashSet<String>();//loaded libraries
    private YZBLinker.LoadListener callback;
    public static boolean exception = false;//for test

    protected YZBLinkerInstance() {
        this(new LinkerLoaderImpl(),new LinkerDownloaderImpl());
    }

    protected YZBLinkerInstance(final YZBLinker.LinkerLoader loader,
                                final YZBLinker.LinkerDownloader downloader) {
        if (loader == null || downloader == null) {
            throw new IllegalArgumentException("Can not pass null to instance YZBLinkerInstance");
        }

        this.linkerLoader = loader;
        this.linkerDownloader = downloader;
    }

    public void loadLibrary(final Context context,
                            final String library) {
        loadLibrary(context, library, null);
    }

    public void loadLibrary(final Context context,
                            final String library,
                            final YZBLinker.LoadListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Can not pass null to method loadLibrary");
        }

        if (TextUtils.isEmpty(library)) {
            throw new IllegalArgumentException("Can not fetch valid library name");
        }

        callback = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(callback == null) {
                    loadLibraryInternal(context, library);
                    return;
                }
                try {
                    if(loadLibraryInternal(context, library))
                        callback.success();
                } catch (UnsatisfiedLinkError e) {
                    callback.failure(e);
                } catch (Exception e) {
                    callback.failure(e);
                }
            }
        }).start();
    }

    private boolean loadLibraryInternal(final Context context, final String library) {
        if(loadedLibraries.contains(library))
            return true;

        try {
            //try to load by system
            exception = true;
            linkerLoader.loadLibrary(library);
            exception = false;
            loadedLibraries.add(library);
            return true;
        } catch (final UnsatisfiedLinkError ignore) {
            //ignore
        }

        //can not load from apk
        final String libName = linkerLoader.mapLibraryName(library);
        if(TextUtils.isEmpty(libName)) return false;
        final File libFile = new File(getLinkerCacheDir(context), libName);
        if(libFile == null) return false;
        if(libFile.exists()) {
            //already cached then load
            linkerLoader.loadPath(libFile.getAbsolutePath());
            loadedLibraries.add(library);
        }else {
            //download then load
            linkerDownloader.downloadLibrary(context, linkerLoader.supportedAbis(), linkerLoader.mapLibraryName(library), libFile,
                    new YZBLinker.DownloadListener() {

                        @Override
                        public void success() {
                            //load from path again
                            linkerLoader.loadPath(libFile.getAbsolutePath());
                            loadedLibraries.add(library);
                            //success
                            if(callback != null)
                                callback.success();
                        }

                        @Override
                        public void failure(Throwable t) {
                            //failure
                            if(callback != null)
                                callback.failure(t);
                        }
                    });
            return false;
        }
        return true;
    }

    private File getLinkerCacheDir(final Context context) {
        return context.getDir(LIB_DIR, Context.MODE_PRIVATE);
    }

}
