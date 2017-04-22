# Zxing Support

### 前言
------
目前二维码扫描十分普遍，Android平台上基本使用Zxing二次开发，但是原生Zxing库使用麻烦，该项目为了方便开发者快速使用。

本项目目的是为了方便开发者使用Zxing，项目是2015年开始，按照[Zxing官方项目](https://github.com/zxing/zxing)修改而来，原官方项目使用消息实现通讯，查看源码时查找实现代码比较麻烦，该项目修改成方法调用。

当初只完成了大概的框架，没有完善接口调用，然后一直搁置。最近有时间把项目拿出来，修改了一些bug，完善了接口并封装成容易调用的库。

### 项目结构
------
项目包含以下两个Module：

app：这个Module是一个demo，参照DemoActivity可以实现快速开发。

zxing：这个Module就是Zxing Support的源码。

### 快速入门
------
第一步：初始化

```Java
DecodeRequest request = new DecodeRequest();//需要判断的类型，此处选择默认类型
zxing = new ZxingSupport(this, request);
zxing.setViewfinderView((ViewfinderView) findViewById(R.id.preview_view));//设置预览
zxing.setTorch((ToggleButton) findViewById(R.id.torch));//设置闪光灯，可选
```
第二步：处理生命周期

```Java
@Override
protected void onResume() {
    super.onResume();
    zxing.onResume();
    if (!zxing.isOpen()) {//相机在onResume启动，此处需要判断启动是否失败
        new OnFailDialog(this).show();
    }
}

@Override
protected void onPause() {
    super.onPause();
    zxing.onPause();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    zxing.onDestroy();
}
```

第三步：处理回调

```Java
@Override
public void onScanReady() {//初始化完成
    zxing.requestScan();//开始扫描
}

@Override
public void onScanSuccess(ScanResult result) {//扫描完成
    String scanResult = result.getRawText();//扫描所得原生数据
}
```

可选：

```Java
BeepManager beep;// 播放哔声
InactivityTimer timer;// 自动关闭

// 自定义扫描的覆盖层
Drawable draw = new ViewfinderView.FrameDrawable(){...}; //FrameDrawable支持定制边框
viewfinderView.setForeground(draw);
```
### License

	Copyright 2017 Yeamy.
		
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
		
	http://www.apache.org/licenses/LICENSE-2.0
		
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
	License for the specific language governing permissions and limitations under
	the License.