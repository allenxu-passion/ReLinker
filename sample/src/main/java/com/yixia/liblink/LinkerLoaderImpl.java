package com.yixia.liblink;

import android.os.Build;
import android.text.TextUtils;

final class LinkerLoaderImpl implements YZBLinker.LinkerLoader {

    @Override
    public void loadLibrary(final String name) {
        System.loadLibrary(name);
    }

    @Override
    public void loadPath(final String path) {
        System.load(path);
    }

    @Override
    public String mapLibraryName(final String library) {
        if (library.startsWith("lib") && library.endsWith(".so"))
            return library;
        return System.mapLibraryName(library);
    }

    @Override
    public String[] supportedAbis() {
        if (Build.VERSION.SDK_INT >= 21 && Build.SUPPORTED_ABIS.length > 0) {
            return Build.SUPPORTED_ABIS;
        } else if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
            return new String[] {Build.CPU_ABI, Build.CPU_ABI2};
        } else {
            return new String[] {Build.CPU_ABI};
        }
    }
}
