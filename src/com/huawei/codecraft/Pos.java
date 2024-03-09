package com.huawei.codecraft;


/**
 * Pos
 * 表示坐标对象,包含x、y坐标
 * 方法:X()、Y()
 */
public class Pos {
    private int x;
    private int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pos(Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public int X() {
        return x;
    }

    public int Y() {
        return y;
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
        return x == pos.x && y == pos.y;
    }

    public int distance(Pos pos) {
        return Math.abs(x - pos.x) + Math.abs(y - pos.y);
    }

    public Pos clone() {
        return new Pos(x, y);
    }

}
