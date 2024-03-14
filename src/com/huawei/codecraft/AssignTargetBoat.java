package com.huawei.codecraft;
import java.util.ArrayList;

/**
 *
 */
public interface AssignTargetBoat {
    void assign(Frame frame);
    public class greedyAssignTarget implements AssignTargetBoat {
        @Override
        public void assign(Frame frame) {
            Boat[] boats=frame.getBoats();//获取船列表
            Goods[] goodsList= frame.getGoods();//获取货物列表
            Berth[] berths= Main.berths;

            for(Boat boat:boats){
                if(boat.getState()==0){
                    boat.setAction(0);
                    continue;}//此时船只处于移动中，相当于消失
                if(boat.getState()==1){
                    //初始化船只状态为state=1，TargetBerthId=-1
                    if(boat.getTargetBerthId() == -1){
                        boat.setState(2);
                    }
                    //暂时先让船随机开走
                    else if (frame.getFrameNumber() % 3 * berths[boat.getTargetBerthId()].getTransportTime() == 0){
                        boat.setAction(2);//相当于go
                        continue;
                    }
                    //此时船只在港口等待,(是否是只要船只已满，就变成状态0了？无需在此处讨论是否已满？)
                    else if(boat.getVacancy()==0){
                        boat.setState(0);
                        boat.setAction(2);//如果此时船只剩余空间为零，将状态设为0
                    }else{//如果还有空间
                        //todo:进行装货物操作
                        //先把下面的移过来，后面再讨论这种state属于哪个
                        int currentBerthId=boat.getTargetBerthId();
                        Berth currentBerth=getBerth(currentBerthId,berths);
                        if(boat.getVacancy()>0){
                            for(Goods goods:goodsList){
                                if(isInsideBerth(currentBerth,goods)){
                                    //找到未被分配的货物
                                    boat.load();
                                    goods.setAssigned(true);
                                }
                            }
                        }
                    }

                }
                if(boat.getState()==2){//此时船只在泊位外等待进入一个泊位
                    //todo:1.是否进入港口 2.选择哪个港口
                    Berth targetBerth=getBerth(boat.getTargetBerthId(),berths);
                    if(targetBerth.getFirstWaitingBoat().getId()==boat.getId()){
                        //如果目标港口等待序列的第一只船就是本船，进入港口
                        boat.setState(1);
                        boat.setAction(1);
                    }else{
                        //如果第一只不是本船，找到最优的港口 ship过去
                        Berth bestBerth=findBestBerth(berths,boats);
                        if(bestBerth!=null){
                            boat.setTargetBerthId(bestBerth.getId());
                        }
                        boat.setAction(1);//ship to targetBerth
                    }

                }
            }
        }
        public boolean isInsideBerth(Berth berth,Goods goods){
            return (goods.getPos().X() == berth.getPos().X() || goods.getPos().X() == berth.getPos().X() + 1 || goods.getPos().X() == berth.getPos().X() + 2 || goods.getPos().X() == berth.getPos().X() + 3) && (goods.getPos().Y() == berth.getPos().Y() || goods.getPos().Y() == berth.getPos().Y() + 1 || goods.getPos().Y() == berth.getPos().Y() + 2 || goods.getPos().Y() == berth.getPos().Y() + 3);
        }
        public Berth getBerth(int currentBerthId,Berth[] berths){
            for(Berth berth:berths){
                if(berth.getId()==currentBerthId){
                    return berth;
                }
            }
            return null;
        }
        public Berth findBestBerth(Berth[] berths,Boat[] boats){
            //找到最优泊位：如果有空泊位，返回空泊位，否则目前取效率最大的
            Berth bestBerth = null;
            for (Berth berth:berths){
                if(berth.waitingLength()==0){
                    return berth;
                }
            }
            int maxSpeed=Integer.MIN_VALUE;
            for(Berth berth:berths){
                if(berth.getLoadingSpeed()>maxSpeed){
                    maxSpeed=berth.getLoadingSpeed();
                    bestBerth=berth;
                    }
                }

            return bestBerth;
        }
    }
}