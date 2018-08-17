package com.getkeepsafe.relinker.sample;

import android.util.Log;
import android.app.Application;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

public class App extends Application {

    public static boolean blockBlackList = true;

    @Override
    public void onCreate() {
        super.onCreate();

        DexposedBridge.findAndHookMethod(System.class, "loadLibrary", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("allentrack", "DexposedBridge catch : " + param.args[0]);
                if(blockBlackList && "hellojni".equals(param.args[0])) {
                    param.args[0] = "hello";
                    Log.e("allentrack", "DexposedBridge replace to hello");
                }
            }
        });
        Log.e("allentrack", "epic inited");
    }
}
