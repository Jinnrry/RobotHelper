![issues](https://img.shields.io/github/issues/Jinnrry/RobotHelper)
![forks](https://img.shields.io/github/forks/Jinnrry/RobotHelper)
![stars](https://img.shields.io/github/stars/Jinnrry/RobotHelper)
![lincense](https://img.shields.io/github/license/Jinnrry/RobotHelper)
![Codacy grade](https://img.shields.io/codacy/grade/3dce672ecf2c4dbb909e005f8f22cfda)
# RobotHelper

## 这是做什么的？

一个安卓自动化脚本的框架。包含了自动化辅助开发常用的点击，找点，文字识别等功能。具体开发说明参见
[wiki](https://github.com/Jinnrry/RobotHelper/wiki)




## V2.0版本新功能

> 这个版本主要更新了Android app爬虫开发的相关支持

1.新增了界面抓取功能，可以将当前窗口的全部Dom元素输出成Json数据

2.新增了Web Api功能，可以使用Web接口调用相关接口

3.新增拖动操作

[使用指南](https://github.com/Jinnrry/RobotHelper/wiki/%E5%9F%BA%E4%BA%8EWEB%E6%8E%A5%E5%8F%A3%E7%9A%84%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)




## V1.0版本简介

1.新增了图片模板匹配（相当于按键精灵找图功能的升级版），自动处理图像分辨率问题，一次抓图，多分辨率终端可用

2.内置了Tessact-Ocr，不再依赖服务端做文字识别，支持设置黑白名单

3.内置opencv，各种图像处理，轨迹追踪，再也不是难题


## Demo

> 更多使用参见  [`cn.xjiangwei.RobotHelper.GamePackage.Main`](https://github.com/Jinnrry/RobotHelper/blob/master/Android/app/src/main/java/cn/xjiangwei/RobotHelper/GamePackage/Main.java)类代码和Wiki说明

```
Point point = Image.findPointByMulColor(ScreenCaptureUtil.getScreenCap(), "434FD7,65|0|414DDB,90|55|46CDFF,5|86|5FA119");
Robot.tap(point);
```

以上代码将会在屏幕中查找![chrome](./docs/chrome.png)图标，然后点击这个图标（你直接运行这段代码可能不会成功，因为你的手机屏幕尺寸跟我不一样）


```
String s = OcrApi.multLineOcr(ScreenCaptureUtil.getScreenCap(), 0, 0, 200, 30);
MLog.info(s);
```
以上代码将输出(0,0)到(200,30)这个矩形区域内的文字。



## 未来规划

- [ ] 1.Hook系统相关api，修改系统相关常量，使游戏可以在模拟器中运行，并且让游戏无法识别模拟器
- [ ] 2.没了，如有建议可以提issues或者pr