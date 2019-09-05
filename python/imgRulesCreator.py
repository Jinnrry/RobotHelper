import os

import matplotlib

matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import cv2

displayPowerState = os.popen("adb shell /system/bin/screencap -p /sdcard/screenshot.png ").read().strip('\n')
displayPowerState = os.popen("adb pull /sdcard/screenshot.png ./t.png").read().strip('\n')

pickPoint = []

breaknum = 0

# 读取图片并缩放方便显示
img = cv2.imread('./t.png')  # bgr 模式

b, g, r = cv2.split(img)
img2 = cv2.merge([r, g, b])  # rgb 模式
height, width = img.shape[:2]

fig = plt.figure()


def bgr2str(bgr):
    strs = ""
    for j in bgr:
        strs += str(hex(j)).replace('0x', '').upper().zfill(2)
    return strs


def onclick(event):
    global breaknum
    if breaknum >= 2:
        x = int(event.xdata)
        y = int(event.ydata)
        print(x, y)
        # print(img[y, x])
        pickPoint.append([img[y, x], x, y])
    else:
        breaknum += 1


cid = fig.canvas.mpl_connect('button_press_event', onclick)
plt.imshow(img2)
plt.xticks([]), plt.yticks([])  # to hide tick values on X and Y axis 14 plt.show()
plt.show()

firstP = pickPoint[0]
firstX = firstP[1]
firstY = firstP[2]
res = bgr2str(firstP[0])

for item in pickPoint[1:]:
    x = item[1]
    y = item[2]
    color = item[0]
    res += "," + str(x - firstX) + "|" + str(y - firstY) + "|" + bgr2str(color)

print("起始点：" + str(firstX) + "," + str(firstY))

print(res)
