//
// Created by jiangwei on 20-8-16.
//

#include <jni.h>
#include <string>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>


struct input_event {
    struct timeval time;
    __u16 type;
    __u16 code;
    __s32 value;
};

extern "C" JNIEXPORT jstring

JNICALL


#define EVIOCGVERSION        _IOR('E', 0x01, int)            /* get driver version */

Java_cn_xjiangwei_RobotHelper_MainActivity_sendEvent(JNIEnv *env, jobject, jstring arg1,
                                                         jstring arg2, jstring arg3) {
    std::string hello = "Hello from C++";
    int fd;
    int version;
    ssize_t ret;
    struct input_event event;
    fd = open("/dev/input/event0", O_RDWR);
    if (fd < 0) {
        fprintf(stderr, "error");
        return env->NewStringUTF("error1");
    }
    if (ioctl(fd, EVIOCGVERSION, &version)) {
        fprintf(stderr, "error2");
        return env->NewStringUTF("error2");
    }
    memset(&event, 0, sizeof(event));
    event.type = atoi(env->GetStringUTFChars(arg1, NULL));
    event.code = atoi(env->GetStringUTFChars(arg2, NULL));
    event.value = atoi(env->GetStringUTFChars(arg3, NULL));
    ret = write(fd, &event, sizeof(event));
    if (ret < (ssize_t) sizeof(event)) {
        fprintf(stderr, "write event failed");
        return env->NewStringUTF("error3");
    }

    return env->NewStringUTF(hello.c_str());
}