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
        var tv = JsPosedHelpers.getObjectField(activity, "tv");
        var checkbox = JsPosedHelpers.getObjectField(activity, "mCheckBox");
        var btn = JsActivity.findViewById(activity, "btn")
        JsTextView.setText(tv, "Jsposed running:"+Test.test(10));
        JsTextView.setText(btn, "点击跳转");
        JsTextView.setTextColor(tv, 0xffff0000);
        JsTextView.setTextSize(tv, 20);
        JsTextView.setOnclickListener(tv, function (view) {
            JsCompoundButton.toggle(checkbox)
        });
        JsView.setOnclickListener(btn, function (view) {
            JsContext.startActivity(activity, "com.wrbug.jsposeddemo.Main2Activity", {
                "a": "test1",
                "b": ["test2","java.lang.String"],
                "c": 12345,
                "d":[12345,"long"]
            })
        });
        JsCompoundButton.setOnCheckedChangeListener(checkbox, function (view, isChecked) {
            JsTextView.setText(tv, "checkBox status:" + isChecked);
        })
    })
}
function main2Hook() {
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposeddemo.Main2Activity", "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
        var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
        var container = JsActivity.findViewById(activity, 2131165229)
        var keys = ["a", "b", "c", "d"];
        for (var i = 0; i < keys.length; i++) {
            var tv = JsPosedHelpers.newInstance("android.widget.TextView", [activity]);
            JsTextView.setText(tv, JsActivity.getExtras(activity, keys[i]) + "");
            JsTextView.setTextSize(tv, 23);
            JsTextView.setTextColor(tv, 0xffff0000);
            JsTextView.setPadding(tv, 20, 20, 20, 20);
            JsViewGroup.addView(container, tv);
        }
    })
}