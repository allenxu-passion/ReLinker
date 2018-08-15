package com.yixia.liblink;

import android.content.Context;

import java.io.File;

public class YZBLinker {

    private YZBLinker() {}

    public static void loadLibrary(final Context context, final String library) {
        loadLibrary(context, library, null);
    }

    public static void loadLibrary(final Context context,
                                   final String library,
                                   final YZBLinker.LoadListener listener) {

    }

    public interface LoadListener {
        void success();
        void failure(Throwable t);
    }

    public interface LinkerLoader {
        void loadLibrary(String name);
        void loadPath(String path);
        String mapLibraryName(String libraryName);
        String unmapLibraryName(String mappedLibraryName);
        String[] supportedAbis();
    }

    public interface LinkerDownloader {
        void downloadLibrary(Context context, String[] abis, String mappedLibraryName, File destination);
    }

}
