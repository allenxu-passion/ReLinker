package com.yixia.liblink;

import android.content.Context;

import java.io.File;

public class LinkerDownloaderImpl implements YZBLinker.LinkerDownloader {

    @Override
    public void downloadLibrary(Context context,
                                String[] abis,
                                String mappedLibraryName,
                                File destination,
                                YZBLinker.DownloadListener listener) {
        //safely download specific abi .so file to destination
        //TODO
        copyLibrary(context, abis, mappedLibraryName, destination);

        //after success then trigger load
        destination.setReadable(true, false);
        destination.setExecutable(true, false);
        destination.setWritable(true);
        listener.success();
    }

    private void copyLibrary(Context context,
                             String[] abis,
                             String mappedLibraryName,
                             File destination) {

    }

}
