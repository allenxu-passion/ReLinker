package com.getkeepsafe.relinker.sample;

import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import android.app.Application;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;
import com.yixia.liblink.YZBLinkerInstance;

public class App extends Application {

    public static final String ALWAYS_EXIST_LIB = "c";
    public static final List<String> LIB_WHITE_LIST = new ArrayList<String>();
    static {
        LIB_WHITE_LIST.add("hellojni");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DexposedBridge.findAndHookMethod(System.class, "loadLibrary", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("allentrack", "DexposedBridge catch : " + param.args[0]);
                if(!YZBLinkerInstance.exception) {
                    Log.e("allentrack", "DexposedBridge catch and replace : " + param.args[0]);
                    param.args[0] = ALWAYS_EXIST_LIB;
                }
            }
        });
        Log.e("allentrack", "epic inited");
    }
}
