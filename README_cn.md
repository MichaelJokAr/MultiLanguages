
### Android 多语言切换（兼容8.0+） 2.0版本，一句代码完成多语言切换，现在支持第三方包里多语言切换（前提是有对应的语言资源）



### **实现原理**
[多语言实现](https://blog.csdn.net/a1018875550/article/details/79845949)

### **2.0版本**
2.0版本使用Transform API 编译插桩的方式来实现```Activity```,```Service``` 的```attachBaseContext```方法覆盖重写（具体请看[plugin](./plugin)下代码）

- 支持AndroidX
- 支持kotlin
- **不支持Instant Run**

### **使用**
- multi-language.plugin  [![Download](https://api.bintray.com/packages/a10188755550/maven/multi-languages.plugin/images/download.svg)](https://bintray.com/a10188755550/maven/multi-languages.plugin/_latestVersion)

- multi-languages [![Download](https://api.bintray.com/packages/a10188755550/maven/multi-languages/images/download.svg) ](https://bintray.com/a10188755550/maven/multi-languages/_latestVersion)

- 引入gradle plugin
    ```
    classpath 'com.github.jokar:multi-languages.plugin:<latest-version>'
    ```
- app ```buidle.gradle``` 文件引入plugin
    ```
    apply plugin: 'multi-languages'
    ```
    插件配置
    ```
    multiLanguages {
        //可以配置开关来控制是否重写(插件会耗时一部分的编译时间)
        enable = true
        //配置强制重写attachBaseContext方法类（如果类里已经重写了attachBaseContext方法，默认不会覆盖重写）
        overwriteClass = ["com.github.jokar.multilanguages.BaseActivity"] 
    }
    ```
- 导入```Library```
    ```
    implementation 'com.github.jokar:multi-languages:<latest-version>'
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


### 其他： [locales列表](https://github.com/championswimmer/android-locales)


---
### 效果图


![效果图](./image/sample.gif)