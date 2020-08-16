//
// Created by jiangwei on 20-8-16.
//

#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL



Java_cn_xjiangwei_RobotHelper_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}