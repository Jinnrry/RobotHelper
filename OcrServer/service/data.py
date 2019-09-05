# coding:utf-8
import base64
import io
import json
import re

import numpy
import time
import datetime
import numpy as np
from django.http import HttpResponse, HttpRequest
from PIL import Image
from cnocr import CnOcr
import cv2


def log(request):
    info = request.GET.get("info")
    with open("log.log", "a") as f:
        f.write(time.strftime('%Y-%m-%d %H:%M:%S ', time.localtime(time.time())))
        f.write(info)
        f.write("\n")
    return HttpResponse(json.dumps({
        "status": 1,
    }))


def singleLineOcr(request):
    imgs = request.POST.get("img")
    img = imgs.replace(" ", "+")

    res = base64.b64decode(img)
    # res = base64.urlsafe_b64encode(img)
    nparr = np.fromstring(res, np.uint8)
    img = cv2.imdecode(nparr, cv2.COLOR_BGR2RGB)
    imgb = img
    img = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)

    # cv2.imwrite("img.png", imgb)

    ocr = CnOcr()
    res = ocr.ocr(img)

    return HttpResponse(
        "".join(res)
    )


def multLineOcr(request):
    imgs = request.POST.get("img")
    img = imgs.replace(" ", "+")

    res = base64.b64decode(img)
    # res = base64.urlsafe_b64encode(img)
    nparr = np.fromstring(res, np.uint8)
    img = cv2.imdecode(nparr, cv2.COLOR_BGR2RGB)
    imgb = img
    img = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)

    # cv2.imwrite("img.png", imgb)

    ocr = CnOcr()
    res = ocr.ocr(img)

    return HttpResponse(
        json.dumps(res)
    )

def testImg(request):
    imgs = request.POST.get("img")
    img = imgs.replace(" ", "+")

    res = base64.b64decode(img)
    nparr = np.fromstring(res, np.uint8)
    img = cv2.imdecode(nparr, cv2.COLOR_BGR2RGB)

    cv2.imwrite("test.png", img)

    return HttpResponse("")