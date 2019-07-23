### Android 多语言切换（兼容8.0+） 2.0版本，一句代码完成多语言切换，现在支持第三方包里多语言切换（前提是有对应的语言资源）

2.0版本使用Transform API 编译插桩的方式来实现```Activity```,```Service``` 的```attachBaseContext```方法覆盖重写
### 使用
- 引入gradle plugin
    ```
    classpath 'com.github.jokar:multi-languages.plugin:0.0.1'
    ```
- app ```buidle.gradle``` 文件引入plugin
    ```
    apply plugin: 'multi-languages'
    ```
    可以配置开关来控制是否重写(插件会耗时一部分的编译时间)
    ```
    multiLanguages {
        enable = true
    }
    ```
- 导入```Library```
    ```
    implementation 'com.github.jokar:multi-languages:0.0.1'
    ```

- application init
    ```
   public class MultiLanguagesApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        //第一次进入app时保存系统选择语言(为了选择随系统语言时使用，如果不保存，切换语言后就拿不到了）
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(MultiLanguage.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //用户在系统设置页面切换语言时保存系统选择语言(为了选择随系统语言时使用，如果不保存，切换语言后就拿不到了）
        LocalManageUtil.saveSystemCurrentLanguage(getApplicationContext(), newConfig);
        MultiLanguage.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiLanguage.init(new LanguageLocalListener() {
            @Override
            public Locale getSetLanguageLocale(Context context) {
                //返回自己本地保存选择的语言设置
                return LocalManageUtil.getSetLanguageLocale(context);
            }
        });
        MultiLanguage.setApplicationLanguage(this);
    }
    }
    ```

    [LocalManageUtil](./app/src/main/java/com/github/jokar/multilanguages/utils/LocalManageUtil.java)里做的是保存选择的语言设置


以上就完成了初始化了，


### 博客
[多语言实现](https://blog.csdn.net/a1018875550/article/details/79845949)


----
[效果图](https://upload-images.jianshu.io/upload_images/2001124-97c41107c687cfab.gif?imageMogr2/auto-orient/strip)