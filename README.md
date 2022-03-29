![issues](https://img.shields.io/github/issues/Jinnrry/RobotHelper)
![forks](https://img.shields.io/github/forks/Jinnrry/RobotHelper)
![stars](https://img.shields.io/github/stars/Jinnrry/RobotHelper)
![lincense](https://img.shields.io/github/license/Jinnrry/RobotHelper)
![Codacy grade](https://img.shields.io/codacy/grade/3dce672ecf2c4dbb909e005f8f22cfda)
# RobotHelper

## 这是做什么的？

一个安卓自动化脚本的框架。包含了自动化辅助开发常用的点击，找点，文字识别等功能。具体开发说明参见
[wiki](https://github.com/Jinnrry/RobotHelper/wiki)


该框架主要是方便Android游戏、爬虫项目的快速开发。比按键精灵等商业软件扩展性强，比AutoJS等更轻量，更适合二次开发。

支持无缝切换使用无障碍、Root、xposed三种方式提权实现点击等模拟操作。



## Demo

> 更多使用参见  [`cn.xjiangwei.RobotHelper.GamePackage.Main`](https://github.com/Jinnrry/RobotHelper/blob/master/Android/app/src/main/java/cn/xjiangwei/RobotHelper/GamePackage/Main.java)类代码和Wiki说明

```
Point point = Image.findPointByMulColor(ScreenCaptureUtil.getScreenCap(), "434FD7,65|0|414DDB,90|55|46CDFF,5|86|5FA119");
Robot.tap(point);
```

以上代码将会在屏幕中查找  <img src="./docs/chrome.png" width="100px" />  图标，然后点击这个图标（你直接运行这段代码可能不会成功，因为你的手机屏幕尺寸跟我不一样）


```
String s = TessactOcr.img2string(ScreenCaptureUtil.getScreenCap(0,0,200,30), "chi_sim", "", "");
MLog.info("文字识别结果：" + s);
```
以上代码将输出(0,0)到(200,30)这个矩形区域内的文字。

`chi_sim`表示语言为简体中文，默认语言包只有chi_sim和eng(英语)

你可以自己引入TessactOcr所支持的任意语言。[语言包下载](https://github.com/tesseract-ocr/tessdata_best)


### [更新日志](./UPDATE.md)



## 未来规划

- [ ] 1.整体重构，将框架拆分为业务进程和Work进程，业务进程通过消息通知的方式向Work进程发送请求
- [ ] 2.支持脚本暂停、停止等操作
- [ ] 3.业务进程、Work进程支持跨机器部署，支持网络调用，方便群控需求
- [ ] 4.业务进程脚本支持热更新

## Thanks

Thanks for all these great works that make this project better.

- [Airtest](https://github.com/AirtestProject/Airtest)
- [Tesseract](https://github.com/tesseract-ocr/tesseract)
- [AutoJS](https://github.com/hyb1996/Auto.js)
- [VTouch](https://github.com/Azard/VTouch/tree/master/app/src/main/java/me/azard/vtouch/event)
