## CHANGELOG

### v0.1.2
- gradle version update to ```6.1.1```
- ASM version update to ```8.0.1```

### v0.1.1
- support Incremental [pull-23](https://github.com/MichaelJokAr/MultiLanguages/pull/23)
### v0.0.8
- 解决```attachBaseContext```方法被重写问题

### v0.0.7
- 去除强制覆盖重写```attachBaseContext```方法逻辑。

    - 如果类里原来重写了该方法需要手动加上

        ``` super.attachBaseContext(MultiLanguage.setLocal(newBase));```，

    - 或者在插件配置里```overwriteClass```里加上全路径包名后插件覆盖重写 

        ```
        multiLanguages {
        enable = true
        overwriteClass = ["com.github.jokar.multilanguages.BaseActivity"]
        }
        ```

### v0.0.6
- 支持androidx
- 支持androidx-v1.1.0版本

