package com.huawei.codecraft;

import java.util.ArrayList;
import java.util.List;

public class Block {
    public Block(int id) {
        this.id = id;
    }

    public static Block[] blocks = new Block[Cons.BLOCK_WIDTH * Cons.BLOCK_WIDTH];
    /**
     * 每行id分配为（按y加1，按x加8）
     * 0-7
     * 8-15
     * 16-23
     * 24-31
     * ……
     */
    private int id;
    List<Goods> goods = new ArrayList<>();//todo:后期可能占用空间过大
    List<List<Block>> neighbours = new ArrayList<>();
    List<List<Pos>> bordersLeft = new ArrayList<>();
    List<List<Pos>> bordersRight = new ArrayList<>();
    List<List<Pos>> bordersUp = new ArrayList<>();
    List<List<Pos>> bordersDown = new ArrayList<>();
    /**
     * 判断a和b是否在同一block
     * @param a
     * @param b
     * @return
     */
    public boolean isSameBlock(Pos a, Pos b) {
        // 0-Cons.BLOCK_SIZE - 1,Cons.BLOCK_SIZE-49, 50-74, 75-99
        return a.X() / Cons.BLOCK_SIZE == b.X() / Cons.BLOCK_SIZE && a.Y() / Cons.BLOCK_SIZE == b.Y() / Cons.BLOCK_SIZE;
    }

    /**
     * 判断边界点a是否与相邻block相连
     * @param a
     * @return
     */
    public static boolean isConnected(Pos a) {
        Map map = Main.map;
        //先判断a的哪个方向是相邻block
        if (a.Y() % Cons.BLOCK_SIZE == 0) {
            //左边界
            return Map.isValidXY(a.X(), a.Y() - 1) && !map.isObstacle(a.X(), a.Y() - 1);
        } else if (a.Y() % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1) {
            //右边界
            return Map.isValidXY(a.X(), a.Y() + 1) && !map.isObstacle(a.X(), a.Y() + 1);
        } else if (a.X() % Cons.BLOCK_SIZE == 0) {
            //上边界
            return Map.isValidXY(a.X() - 1, a.Y()) && !map.isObstacle(a.X() - 1, a.Y());
        } else if (a.X() % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1) {
            //下边界
            return Map.isValidXY(a.X() + 1, a.Y()) && !map.isObstacle(a.X() + 1, a.Y());
        }
        else {
            return false;

        }
    }
    public static boolean isConnected(int x, int y) {
        Map map = Main.map;
        //先判断a的哪个方向是相邻block
        if (y % Cons.BLOCK_SIZE == 0) {
            //左边界
            return Map.isValidXY(x, y - 1) && !map.isObstacle(x, y - 1);
        } else if (y % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1) {
            //右边界
            return Map.isValidXY(x, y + 1) && !map.isObstacle(x, y + 1);
        } else if (x % Cons.BLOCK_SIZE == 0) {
            //上边界
            return Map.isValidXY(x - 1, y) && !map.isObstacle(x - 1, y);
        } else if (x % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1) {
            //下边界
            return Map.isValidXY(x + 1, y) && !map.isObstacle(x + 1, y);
        }
        else {
            return false;

        }
    }

    /**
     * 获取当前areaId的block的邻居blocks
     * @param areaId
     * @return
     */
    public List<Block> getNeighbours(int areaId) {
        return neighbours.get(areaId);
    }
    /**
     * 获取从blockIdFrom到blockIdTo的方向
     * @param blockIdFrom
     * @param blockIdTo
     * @return
     */
    public static int getDirection(int blockIdFrom, int blockIdTo) {
        //0-1向右，1-0向左，0-8向下，8-0向上
        if (blockIdFrom == blockIdTo - 1) {
            return Cons.DIRECTION_RIGHT;
        } else if (blockIdFrom == blockIdTo + 1) {
            return Cons.DIRECTION_LEFT;
        } else if (blockIdFrom == blockIdTo - Cons.BLOCK_WIDTH) {
            return Cons.DIRECTION_DOWN;
        } else if (blockIdFrom == blockIdTo + Cons.BLOCK_WIDTH) {
            return Cons.DIRECTION_UP;
        }
        else {
            return -1;
        }
    }

    /**
     * 获取当前block在指定方向的边界
     * @param areaId
     * @param direction
     * @return
     */
    public List<Pos> getBorders(int areaId, int direction) {
        if (direction == Cons.DIRECTION_RIGHT) {
            return bordersRight.get(areaId);
        } else if (direction == Cons.DIRECTION_LEFT) {
            return bordersLeft.get(areaId);
        } else if (direction == Cons.DIRECTION_UP) {
            return bordersUp.get(areaId);
        } else if (direction == Cons.DIRECTION_DOWN) {
            return bordersDown.get(areaId);
        }
        else{
            return null;
        }
    }
    /**
     * 判断位置a是否在指定方向的边界上
     * @param a
     * @param direction
     * @return
     */
    public static boolean atBorder(Pos a, int direction) {
        if (direction == Cons.DIRECTION_RIGHT) {
            return a.Y() % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1;
        } else if (direction == Cons.DIRECTION_LEFT) {
            return a.Y() % Cons.BLOCK_SIZE == 0;
        } else if (direction == Cons.DIRECTION_UP) {
            return a.X() % Cons.BLOCK_SIZE == 0;
        } else if (direction == Cons.DIRECTION_DOWN) {
            return a.X() % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1;
        }
        else{
            return false;
        }
    }
    public boolean atBorder(int x, int y , int direction) {
        if (direction == Cons.DIRECTION_RIGHT) {
            return y % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1;
        } else if (direction == Cons.DIRECTION_LEFT) {
            return y % Cons.BLOCK_SIZE == 0;
        } else if (direction == Cons.DIRECTION_UP) {
            return x % Cons.BLOCK_SIZE == 0;
        } else if (direction == Cons.DIRECTION_DOWN) {
            return x % Cons.BLOCK_SIZE == Cons.BLOCK_SIZE - 1;
        }
        else{
            return false;
        }
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setNeighbours(List<List<Block>> neighbours) {
        this.neighbours = neighbours;
    }
    public void setBordersLeft(List<List<Pos>> bordersLeft) {
        this.bordersLeft = bordersLeft;
    }
    public void setBordersRight(List<List<Pos>> bordersRight) {
        this.bordersRight = bordersRight;
    }
    public void setBordersUp(List<List<Pos>> bordersUp) {
        this.bordersUp = bordersUp;
    }
    public void setBordersDown(List<List<Pos>> bordersDown) {
        this.bordersDown = bordersDown;
    }
    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }
    public List<Goods> getGoods() {
        return goods;
    }
    public void addGoods(Goods good) {
        goods.add(good);
    }
    public void removeGoods(Goods good) {
        goods.remove(good);
    }
    public void newGoods() {
        goods = new ArrayList<>();
    }
    public static int getBlockId(Pos pos) {
        return pos.X() / Cons.BLOCK_SIZE * Cons.BLOCK_WIDTH + pos.Y() / Cons.BLOCK_SIZE;
    }
}
