package cn.xjiangwei.RobotHelper.Tools;

public class Random {

    /**
     * 获取一个范围的随机数
     * @param start
     * @param end
     * @return
     */
    public static int randomInt(int start, int end) {
        java.util.Random r = new java.util.Random();
        return r.nextInt(end - start) + start;
    }
}
