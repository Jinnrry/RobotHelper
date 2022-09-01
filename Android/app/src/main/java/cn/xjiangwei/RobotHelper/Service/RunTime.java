package cn.xjiangwei.RobotHelper.Service;

import cn.xjiangwei.RobotHelper.GamePackage.Main;

public class RunTime {

    private static volatile RunTime instance;
    private Thread runtime;

    private RunTime() {
        runtime = new Thread(() -> {
            new Main().start();
        });
    }

    //获取一个运行实例
    public static RunTime getInstance() {
        synchronized (RunTime.class) {
            if (instance == null) {
                // 从来没有运行过
                instance = new RunTime();
            } else {
                // 如果有实例正在运行，返回运行中的实例
                if (instance.isRunning()) {
                    return instance;
                } else {
                    // 实例运行结束了，返回新的实例
                    instance = new RunTime();
                }
            }


            return instance;

        }

    }


    public void start() {
        runtime.start();
    }


    public void stop() {
        runtime.interrupt();
    }


    public boolean isRunning() {
        return runtime.isAlive();
    }
}
