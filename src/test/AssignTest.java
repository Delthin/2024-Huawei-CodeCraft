package test;

import com.huawei.codecraft.*;
import org.junit.jupiter.api.Test;

public class AssignTest {
    public static Frame frameSimpleInit(){
        Map map = MapUtils.mapInit();
        Frame frame = new Frame(1, map);
        Goods[] goods = new Goods[10];
        for (int i = 0; i < 10; i++) {
            goods[i] = new Goods(i, 0, 100, 1);
        }
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            robots[i] = new Robot(i,0,i,5,1);
            robots[i].assignTargetGoods(goods[i]);
        }
        frame.updateRobots(robots);
        frame.updateGoods(goods);
        return frame;
    }
    public static void printRobotAndTarget(Frame frame){
        Robot[] robots = frame.getRobots();
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            if (robot.isHasGoods()){
                System.out.println("Robot "+robot.getId()+" has goods "+robot.getTargetGoods().getValue() + " and "+ robot.getTargetGoods().getPos());
            }else {
                System.out.println("Robot "+robot.getId()+" has target "+robot.getTargetPos());
            }
        }
    }
    @Test
    public void simpleTest(){
        Frame frame = frameSimpleInit();
        AssignTarget assignTarget = new AssignTarget.greedyAssignTarget();
        assignTarget.assign(frame);
        Robot[] robots = frame.getRobots();
        printRobotAndTarget(frame);
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            if (robot.isHasGoods()){
                assert robot.getTargetGoods()!=null;
            }else {
                assert robot.getTargetGoods()!=null;
            }
        }
    }
}
