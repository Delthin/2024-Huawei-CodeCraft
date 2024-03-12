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
                if(boat.getState()==0)continue;;//此时船只处于移动中，相当于消失
                if(boat.getState()==1){
                    //初始化船只状态为state=1，TargetBerthId=-1
                    if(boat.getTargetBerthId() == -1){
                        boat.setState(2);
                    }
                    //此时船只在港口等待,(是否是只要船只已满，就变成状态0了？无需在此处讨论是否已满？)
                    if(boat.getVacancy()==0){
                        boat.setAction(2);//如果此时船只剩余空间为零，将状态设为0
                    }else{//如果还有空间
                        //todo:进行装货物操作
                        //先把下面的移过来，后面再讨论这种state属于哪个

                    }

                }
                if(boat.getState()==2){//此时船只在泊位外等待进入一个泊位
                    //todo:判断进入哪一个港口
                    Berth bestBerth=findBestBerth(berths,boats);
                    if(bestBerth!=null){
                        boat.setTargetBerthId(bestBerth.getId());
                        boat.setAction(1);
                    }

                }
            }
        }
        public Berth findBestBerth(Berth[] berths,Boat[] boats){
            //找到最优泊位：前提为空，取效率最大的
            Berth bestBerth = null;
            boolean isEmpty=true;
            ArrayList<Berth> emptyBerthList = new ArrayList<>();
            int index=0;
            for(Berth berth:berths){
                for(Boat boat:boats){
                    if(boat.getState()==1&&boat.getTargetBerthId()==berth.getId()){
                        isEmpty=false;
                    }
                }
                if(!isEmpty){
                    continue;
                }else{//确实为空
                    emptyBerthList.add(berth);
                    index++;
                }
            }
            int maxSpeed=Integer.MIN_VALUE;
            if(!emptyBerthList.isEmpty()){
                for(Berth berth:emptyBerthList){
                    if(berth.getLoadingSpeed()>maxSpeed){
                        maxSpeed=berth.getLoadingSpeed();
                        bestBerth=berth;
                    }
                }
            }
            return bestBerth;
        }
    }
}
