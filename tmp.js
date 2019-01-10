function start() {
    var packageName = Env.packageName();
    if (packageName != "com.wrbug.jsposed") {
        return
    }
    JsPosedBridge.log("Jsposed running");
    JsPosedBridge.log("loadPackage", Env.packageName());
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposed.MainActivity", "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
        var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
        var tv = JsPosedHelpers.getObjectField(activity, "tv");
        var checkbox = JsPosedHelpers.getObjectField(activity, "mCheckBox");
        var btn = JsContext.findViewById(activity, 0x7f070022)
        JsView.setText(tv, "Jsposed running");
        JsView.setText(btn, "点击跳转");
        JsView.setTextColor(tv, 0xffff0000);
        JsView.setTextSize(tv, 20);
        JsView.setOnclickListener(tv, function (view) {
            JsView.toggle(checkbox)
        });
        JsView.setOnclickListener(btn, function (view) {
            JsContext.startActivity(activity, "com.wrbug.jsposed.Main2Activity", {
                "a": "test1",
                "b": ["test2","java.lang.String"],
                "c": 12345,
                "d":[12345,"long"]
            })
        });
        JsView.setOnCheckedChangeListener(checkbox, function (view, isChecked) {
            JsView.setText(tv, "checkBox status:" + isChecked);
        })
    })
    main2Hook();
}

function main2Hook() {
    JsPosedHelpers.findAndHookMethod("com.wrbug.jsposed.Main2Activity", "onCreate", ["android.os.Bundle"], null, function (methodHookParam) {
        var activity = JsPosedHelpers.getObjectField(methodHookParam, "thisObject");
        var container = JsContext.findViewById(activity, 2131165229)
        var keys = ["a", "b", "c", "d"];
        for (var i = 0; i < keys.length; i++) {
            var tv = JsPosedHelpers.newInstance("android.widget.TextView", [activity]);
            JsView.setText(tv, JsContext.getExtra(activity, keys[i]) + "");
            JsView.setTextSize(tv, 23);
            JsView.setTextColor(tv, 0xffff0000);
            JsView.setPadding(tv, 20, 20, 20, 20);
            JsViewGroup.addView(container, tv);
        }
    })
}