## V2.4.2版本新功能

1.修复截图被系统GC后出现的`recycled bitmap` bug

2、支持了Android 11及以上版本的输入Hook

## V2.4.1版本新功能

1、添加了退出程序的按钮

2、优化了代码结构，让代码稍稍能看懂一点了

## V2.4版本新功能

截屏函数现在将会自动识别屏幕方向了！

由于以前全部都是横屏模式，如果你需要兼容以前的代码你可以使用ScreenCaptureUtil.setHorizontalScreen();强制将截图指定为横屏模式


## V2.3版本新功能

1.加入双指缩放功能

感谢chongkaechin实现。

https://github.com/Jinnrry/RobotHelper/issues/13


## V2.2版本新功能

1.加入了Root权限实现点击操作，目前仅在oneplus 7pro上测试通过，不保证所有手机兼容。遇到无法使用的问题欢迎提bug


## V2.1版本新功能

1.底层点击实现新增了无障碍接口实现,你可以使用`Robot.setExecType(Robot.ExecTypeXposed);`或者`Robot.setExecType(Robot.ExecTypeAccessibillty);`切换实现方式

2.代码整理（But 整理后依然很乱）


## V2.0版本新功能

> 这个版本主要更新了Android app爬虫开发的相关支持

1.新增了界面抓取功能，可以将当前窗口的全部Dom元素输出成Json数据

2.新增了Web Api功能，可以使用Web接口调用相关接口

3.新增拖动操作

[使用指南](https://github.com/Jinnrry/RobotHelper/wiki/%E5%9F%BA%E4%BA%8EWEB%E6%8E%A5%E5%8F%A3%E7%9A%84%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)


## V1.0版本

1.新增了图片模板匹配（相当于按键精灵找图功能的升级版），自动处理图像分辨率问题，一次抓图，多分辨率终端可用

2.内置了Tessact-Ocr，不再依赖服务端做文字识别，支持设置黑白名单

3.内置opencv，各种图像处理，轨迹追踪，再也不是难题
