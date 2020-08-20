package main

import (
	"encoding/binary"
	"fmt"
	"log"
	"os"
	"syscall"
)

/**
使用go语言重新实现了android的sendevent工具
与原本sendevent有一点差别，原本sendevent每次调用都会重新开一个进程，然后重新打开一次文件，从而造成很大的资源浪费。
我实现的这个版本会一直等待输入，不会关闭进程，也不会关闭文件
这个文件参考了https://android.googlesource.com/platform/system/core/+/android-5.0.2_r3/toolbox/sendevent.c 这个是android sendevent工具源码
*/

type InputEvent struct {
	Time  syscall.Timeval
	Type  uint16
	Code  uint16
	Value int32
}

func init() {
	//f, err := os.OpenFile("/data/user/0/cn.xjiangwei.RobotHelper/cache/input.log", os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
	//if err != nil {
	//	log.Fatalf("error opening file: %v", err)
	//}
	//log.SetOutput(f)
}

func main() {
	if _, err := os.Stat(os.Args[1]); err == nil {
		log.Println(os.Args[1])
		fmt.Println(os.Args[1])
		inputEvent := InputEvent{}
		device, err := os.OpenFile(os.Args[1], os.O_RDWR, 0777)
		if err != nil {
			fmt.Println("ERROR!" + err.Error())
			log.Println("ERROR!" + err.Error())
			return
		} else {
			defer device.Close()
			for true {
				_, err = fmt.Scanln(&inputEvent.Type, &inputEvent.Code, &inputEvent.Value)
				if err != nil {
					fmt.Println("ERROR" + err.Error())
					log.Println("ERROR" + err.Error())
				} else {
					err = binary.Write(device, binary.LittleEndian, inputEvent)
					log.Println(inputEvent)
					if err != nil {
						fmt.Println("ERROR" + err.Error())
						log.Println("ERROR" + err.Error())
					}
				}
			}
		}
	} else {
		fmt.Println(err.Error())
		log.Println(err.Error())
	}

}
