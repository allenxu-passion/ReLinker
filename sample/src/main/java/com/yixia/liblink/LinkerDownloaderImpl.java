package com.yixia.liblink;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class LinkerDownloaderImpl implements YZBLinker.LinkerDownloader {

    @Override
    public void downloadLibrary(Context context,
                                String[] abis,
                                String library,
                                File destination,
                                YZBLinker.DownloadListener listener) {
        if(TextUtils.isEmpty(library) || destination == null || listener == null)
            return;

        try {
            if (!destination.exists() && !destination.createNewFile()) {
                return;
            }
        } catch (IOException ignored) {
            return;
        }

        //TODO safely download lib to destination


        //test
        copyLibrary(abis, library, destination);


        //download success
        destination.setReadable(true, false);
        destination.setExecutable(true, false);
        destination.setWritable(true);
        listener.success();
    }

    /**
     * This is only for test
     * copy sdcard lib to cache folder
     *
     * @param abis
     * @param library
     * @param destination
     */
    private void copyLibrary(String[] abis,
                             String library,
                             File destination) {
        for (final String abi : abis) {
            String storedLib = "lib" + File.separatorChar + abi + File.separatorChar + library;
            InputStream inputStream = null;
            FileOutputStream fileOut = null;
            try {
                File exFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "allen" + File.separatorChar + storedLib);
                if(exFile.exists())
                    inputStream = new FileInputStream(exFile);
                else
                    continue;
                fileOut = new FileOutputStream(destination);
                final long written = copy(inputStream, fileOut);
                fileOut.getFD().sync();
                if (written != destination.length())
                    continue;//something goes wrong
            } catch (FileNotFoundException e) {
                continue;
            } catch (IOException e) {
                continue;
            } finally {
                closeSafely(inputStream);
                closeSafely(fileOut);
            }
        }
    }

    private long copy(InputStream in, OutputStream out) throws IOException {
        long copied = 0;
        byte[] buf = new byte[4096];
        while (true) {
            int read = in.read(buf);
            if (read == -1) {
                break;
            }
            out.write(buf, 0, read);
            copied += read;
        }
        out.flush();
        return copied;
    }

    private void closeSafely(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {}
    }

}
