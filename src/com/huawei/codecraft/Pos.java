package com.huawei.codecraft;


/**
 * Pos
 * 表示坐标对象,包含x、y坐标
 * 方法:X()、Y()
 */
public class Pos {
    private int x;
    private int y;
    public int bfsRealDistance;//实际
    public int bfsWeightsDistance;//权重
    public Berth berth;

    public Pos parent;
    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
        this.bfsRealDistance=Integer.MAX_VALUE;
        this.bfsWeightsDistance=Integer.MAX_VALUE;
    }

    public Pos(Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
    }
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
    public int X() {
        return this.x;
    }

    public int Y() {
        return this.y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPos(Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public boolean equals(Pos pos) {
        return this.x == pos.x && this.y == pos.y;
    }

    public int Mdistance(Pos pos) {
        return Math.abs(x - pos.X()) + Math.abs(y - pos.Y());
    }

    public Pos clone() {
        return new Pos(x, y);
    }


}
