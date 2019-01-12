# JsPosed

> JsPosed 是一款通过js代码调用xposed相关功能的框架，支持热部署，热更新，无需重启即可生效


### 下载体验

1. 编译项目或 [下载apk](https://github.com/WrBug/JsPosed/releases/tag/demo) ，安装到有xposed环境的设备上

2. 下载[tmp.js](https://github.com/WrBug/JsPosed/blob/master/tmp.js) 导入到手机 `/data/local/tmp` 目录下
3. 第一次需要激活重启，重启后再次打开 app即可生效
4. 可以通过修改 `tmp.js` 内容，保存后杀掉app进程再启动即可生效

![](https://i.loli.net/2019/01/10/5c37115ead403.png)![](https://i.loli.net/2019/01/10/5c37115f50b03.png)

### wiki

完整使用说明请查看[wiki](https://github.com/WrBug/JsPosed/wiki)

## 开始使用

### 配置依赖

```
implementation 'com.wrbug:jsposed:0.0.1'

```
 
#### 初始化



``` java
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        String js=...
        boolean debug=...
        //初始化JsPosed
        JsPosedExecutor executor = JsPosedExecutor.init(lpparam, jsContent, debug);
        //执行js方法
        executor.run(fun);
    }
```
###### 参数说明

| 参数 | 说明 |
| --- | --- |
| lpparam | handleLoadPackage方法参数  |
| jsContent | js文本 |
| debug | 打印debug日志 |
| fun | 执行方法，一般为入口方法 |

###### 注意

js文本可以使用硬编码或通过文件的形式获取,使用文件时需注意hook的应用是否有权限读取文件.
可以参考[XposedInit.java](https://github.com/WrBug/JsPosed/blob/master/sample/src/main/java/com/wrbug/jsposeddemo/XposedInit.java#L18)

#### 简单js实现

```
function start(){
    var packageName = Env.packageName();
    if (packageName != "com.wrbug.jsposed") {
        return
    }
    
    //hook MainActivity#onCreate 方法
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposeddemo.MainActivity", "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
            //获取thisObject变量
            var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
            // 获取tv变量
            var tv = JsPosedHelpers.getObjectField(activity, "tv");
            // 获取mCheckBox变量
            var checkbox = JsPosedHelpers.getObjectField(activity, "mCheckBox");
            //通过 idName获取btn
            var btn = JsContext.findViewById(activity, "btn")
            // 设置tv文本
            JsView.setText(tv, "Jsposed running");
            JsView.setText(btn, "点击跳转");
            // 设置tv文本颜色
            JsView.setTextColor(tv, 0xffff0000);
            // 设置tv字体大小
            JsView.setTextSize(tv, 20);
            // 设置tv 点击事件
            JsView.setOnclickListener(tv, function (view) {
                JsView.toggle(checkbox)
            });
            JsView.setOnclickListener(btn, function (view) {
                // 跳转到 Main2Activity
                JsContext.startActivity(activity, "com.wrbug.jsposeddemo.Main2Activity", {
                    "a": "test1",
                    "b": ["test2","java.lang.String"],
                    "c": 12345,
                    "d":[12345,"long"]
                })
            });
            // checkbox 事件监听
            JsView.setOnCheckedChangeListener(checkbox, function (view, isChecked) {
                JsView.setText(tv, "checkBox status:" + isChecked);
            })
        })
    }

```

我们在框架内置了许多Js方法，大多数方法与原生方法命名相同，由于js无法操作原生对象，所以Js方法第一个参数一般穿对象实例，完整方法请查看方法对照表


