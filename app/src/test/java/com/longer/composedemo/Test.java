package com.longer.composedemo;

/**
 * @Author: longer
 * @Date: 2023-03-02 19:18
 * @Description:
 */


public class Test {

    /**
     * 方法接收三个 Point 类型的参数，代表三个点的坐标。方法内部首先使用 atan2 函数计算出第一个点和第二个点之间的线段与水平方向的夹角，
     * 再计算出第一个点和第三个点之间的线段与水平方向的夹角。两个角度的差即为第三个点与线段之间的夹角。
     * 最后需要注意，如果夹角大于180度，则需要减去360度，如果夹角小于等于-180度，
     * 则需要加上360度，以确保夹角落在-180度到180度之间，同时也支持钝角的计算。
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public static double calculateAngle(Point p1, Point p2, Point p3) {
        double angle1 = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        double angle2 = Math.atan2(p3.y - p1.y, p3.x - p1.x);
        double angle = angle2 - angle1;
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        } else if (angle <= -Math.PI) {
            angle += 2 * Math.PI;
        }
        // Convert to degrees
        angle = angle * 180 / Math.PI;

        return angle;
    }

    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


    public static void main(String[] args) {
        Point point1 = new Point(1, 1);
        Point point2 = new Point(1, 0);
        Point point3 = new Point(2, 0);

        double angle = calculateAngle(point1, point2, point3);

        System.out.println("The angle between the line passing through point1 and point2, and point3 is " + (angle)  + " degrees.");

        if (angle > 180) {
            angle = 360 - angle;
        } else if (angle < 0) {
            angle = 360 + angle;
        }
        System.out.println("The angle between the line passing through point1 and point2, and point3 is " + (angle) + " degrees.");
    }
}
