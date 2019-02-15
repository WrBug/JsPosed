function start() {
    var packageName = Env.packageName();
    if (packageName != "com.wrbug.jsposed") {
        return
    }
    JsPosedBridge.log("Jsposed running");
    JsPosedBridge.log("loadPackage:", Env.packageName());
    JsPosedBridge.log("processName:", Env.processName());
    JsPosedBridge.log("isFirstApplication:", Env.isFirstApplication());
    JsPosedBridge.log("sdk:", Env.sdkInit());
    JsPosedBridge.log("sdk:", Env.buildVersion("SDK_INT"));
    mainHook();
    main2Hook();
}
function mainHook(){
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposeddemo.MainActivity",Env.classLoader(), "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
        var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
        var jsActivity=JsActivity.wrapperInstance(activity);
        var tv = JsPosedHelpers.getObjectField(activity, "tv");
        var checkbox = JsPosedHelpers.getObjectField(activity, "mCheckBox");
        var jsCb=JsCheckBox.wrapperInstance(checkbox);
        var btn = jsActivity.findViewById(0x7f070022)
        var button=JsButton.wrapperInstance(btn);
        var textView=JsTextView.wrapperInstance(tv);
        textView.setText("Jsposed running:"+Test.test(10));
        button.setText("点击跳转1");
        textView.setTextColor(0xffff0000);
        textView.setTextSize(20);
        textView.setOnClickListener(function (view) {
            jsCb.toggle()
            var intent=JsIntent.newInstance();
            intent.putExtra("aaa","1234");
        });
        button.setOnClickListener(function (view) {
            var intent=JsIntent.newInstance();
            intent.setClassName(activity, "com.wrbug.jsposeddemo.Main2Activity");
            intent.putExtra("a","test1");
            intent.putExtra("b","test2");
            intent.putExtra("c",12345);
            intent.putExtra("d",12345);
            jsActivity.startActivity(intent.getRealValue());
        });
        jsCb.setOnCheckedChangeListener( function (view, isChecked) {
            textView.setText("checkBox status:" + isChecked);
        })
    })
}
function main2Hook() {
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposeddemo.Main2Activity", "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
        var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
        var jsActivity=JsActivity.wrapperInstance(activity);
        var containerWrapper = JsViewGroup.wrapperInstance(jsActivity.findViewById(0x7f07002d))
        var keys = ["a", "b", "c", "d"];
        var intent=JsIntent.wrapperInstance(jsActivity.getIntent());
        var bundle=JsBundle.wrapperInstance(intent.getExtras());
        for (var i = 0; i < keys.length; i++) {
            // var tv = JsPosedHelpers.newInstance("android.widget.TextView", [activity]);
            var textView=JsTextView.newInstance(activity)
            textView.setText( bundle.get(keys[i]) + "");
            textView.setTextSize(23);
            textView.setTextColor( 0xffff0000);
            textView.setPadding( 20, 20, 20, 20);
            containerWrapper.addView(textView.getRealValue());
        }
    })
}