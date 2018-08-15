package com.yixia.liblink;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class YZBLinkerInstance {

    private static final String LIB_DIR = "yzblibs";
    protected final YZBLinker.LinkerLoader linkerLoader;
    protected final YZBLinker.LinkerDownloader linkerDownloader;
    protected final Set<String> loadedLibraries = new HashSet<String>();

    protected YZBLinkerInstance() {
        this(null,null);
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
                            final String library,
                            final YZBLinker.LoadListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Can not pass null to method loadLibrary");
        }

        if (TextUtils.isEmpty(library)) {
            throw new IllegalArgumentException("Can not fetch valid library name");
        }

        if (listener == null) {
            loadLibraryInternal(context, library);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadLibraryInternal(context, library);
                        listener.success();
                    } catch (UnsatisfiedLinkError e) {
                        listener.failure(e);
                    } catch (Exception e) {
                        listener.failure(e);
                    }
                }
            }).start();
        }
    }

    private void loadLibraryInternal(final Context context, final String library) {
        if(loadedLibraries.contains(library)) return;

        try {
            linkerLoader.loadLibrary(library);
            loadedLibraries.add(library);
            return;
        } catch (final UnsatisfiedLinkError e) {
        }

        //can not load from apk
        final String libName = linkerLoader.mapLibraryName(library);
        final File libFile = new File(getLinkerCacheDir(context), libName);
        if(libFile.exists()) {
            //load
            linkerLoader.loadPath(libFile.getAbsolutePath());
        }else {
            //download then load
            linkerDownloader.downloadLibrary(context, linkerLoader.supportedAbis(), linkerLoader.mapLibraryName(library), libFile);
        }
    }

    private File getLinkerCacheDir(final Context context) {
        return context.getDir(LIB_DIR, Context.MODE_PRIVATE);
    }

}
