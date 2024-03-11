package com.huawei.codecraft;

import com.sun.jndi.ldap.Ber;

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
            Berth[] berths= frame.getBerths();

            for(Boat boat:boats){
                if(boat.getState()==0)continue;;//此时船只处于移动中，相当于消失
                if(boat.getState()==1){
                    //此时船只在港口等待,(是否是只要船只已满，就变成状态0了？无需在此处讨论是否已满？)
                    if(boat.getVacancy()==0){
                        boat.setState(0);//如果此时船只剩余空间为零，将状态设为0
                    }else{//如果还有空间
                        //todo:进行装货物操作

                    }

                }
                if(boat.getState()==2){
                    //此时船只在泊位外等待进入一个泊位
                    

                }
            }
        }
    }
}
