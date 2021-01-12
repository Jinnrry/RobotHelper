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

    public static RunTime getInstance() {
        if (instance == null) {
            synchronized (RunTime.class) {
                if (instance == null) {
                    instance = new RunTime();
                }
            }
        }
        return instance;
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
