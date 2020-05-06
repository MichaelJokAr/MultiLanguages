
### Android multi-language（support android O+） [中文](./README_cn.md)

support third library  multi-language( if there is a corresponding language resource file) ,in version 2.0.


### **version 2.0**
Uses the Transform API to compile instrumentation to achieve ```attachBaseContext``` method auto insert of  ```Activity``` and ```Service``` (code is in [plugin](./plugin))

- support androidx
- support kotlin
- **Not support Instant Run**


### **Uses**
- multi-language.plugin  [![Download](https://api.bintray.com/packages/a10188755550/maven/multi-languages.plugin/images/download.svg)](https://bintray.com/a10188755550/maven/multi-languages.plugin/_latestVersion)

- multi-languages [![Download](https://api.bintray.com/packages/a10188755550/maven/multi-languages/images/download.svg) ](https://bintray.com/a10188755550/maven/multi-languages/_latestVersion)

- Import gradle plugin
    ```
    classpath 'com.github.jokar:multi-languages.plugin:<latest-version>'
    ```
- apply plugin in app ```buidle.gradle``` file
    ```
    apply plugin: 'multi-languages'
    ```
    gradle configuration
    ```
    multiLanguages {
        //set plugin is enable( default)
        enable = true 
    }
    ```
- import ```Library```
    ```
    implementation 'com.github.jokar:multi-languages:<latest-version>'
    ```

- application init
    ```
   public class MultiLanguagesApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        //Save the system language selection when entering the app for the first time.
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(MultiLanguage.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
        The user saves the system selection language when switching languages on the system settings page (in order to select when the system language is used, if it is not saved, it will not be available after switching languages)
        **/
        LocalManageUtil.saveSystemCurrentLanguage(getApplicationContext(), newConfig);
        MultiLanguage.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiLanguage.init(new LanguageLocalListener() {
            @Override
            public Locale getSetLanguageLocale(Context context) {
                //return your local settings
                return LocalManageUtil.getSetLanguageLocale(context);
            }
        });
        MultiLanguage.setApplicationLanguage(this);
    }
    }
    ```

    sample of save multi-language select [LocalManageUtil](./app/src/main/java/com/github/jokar/multilanguages/utils/LocalManageUtil.java)


**then is done**



### Other: [locales list](https://github.com/championswimmer/android-locales)

---
### **sample image**
![sample-image](./image/sample.gif)