package com.getkeepsafe.relinker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.yixia.liblink.YZBLinker;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("hellojni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * TODO ALLEN
         * 1）首次需要正常加载一个so文件，以加载系统so，如libstdc++.so, libm.so, libc.so, libdl.so
         * 否则如果利用依赖解析，加载依赖的各个系统so，如果某个加载失败（如libdl.so），则导致用户so无法正常加载（dlopen failed: "/data/data/xxx/app_lib/xxx.so" is 32-bit instead of 64-bit）
         * 因此最佳实践是，先成功加载一个so文件，引导系统so正确加载完毕。（貌似打包时libs下存在一个so，而代码里不加载，则也可以）
         *
         * 2）嵌套so，最好嵌套实现加载，以免时序错误
         *
         * 3）其他需要考虑
         * - 用户清除缓存
         * - 多用户存储
         * - 库更新
         */

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLibraries();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("allentrack", "result : " + Native.helloJni());
                ((TextView) findViewById(R.id.text)).setText(Native.helloJni());
            }
        });
    }

    private void loadLibraries() {
        for(String lib:App.LIB_WHITE_LIST) {
            final String toLoadLib = lib;
            YZBLinker.loadLibrary(MainActivity.this, toLoadLib, new YZBLinker.LoadListener() {
                @Override
                public void success() {
                    Log.e("allentrack", "load success : " + toLoadLib);
                }

                @Override
                public void failure(Throwable t) {
                    Log.e("allentrack", "load fail : " + toLoadLib);
                }
            });
        }
    }
}
