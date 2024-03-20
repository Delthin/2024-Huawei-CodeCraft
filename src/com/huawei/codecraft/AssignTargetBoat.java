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
            Boat[] boats = frame.getBoats();//获取船列表
            Goods[] goodsList = frame.getGoods();//获取货物列表
            Berth[] berths = Main.berths;

            for (Boat boat : boats) {
                if (boat.getState() == 0) {
                    boat.setAction(0);
                    continue;
                }//此时船只处于移动中，相当于消失
                if (boat.getState() == 1) {
                    //初始化船只状态为state=1，TargetBerthId=-1
                    if (boat.getTargetBerthId() == -1) {
                        boat.setState(2);
                    }
                    //暂时先让船随机开走
                    else if (frame.getFrameNumber() % 3 * berths[boat.getTargetBerthId()].getTransportTime() == 0) {
                        boat.setAction(2);
                        continue;
                    }
                    //此时船只在港口等待,(是否是只要船只已满，就变成状态0了？无需在此处讨论是否已满？)
                    else if (boat.getVacancy() == 0) {
                        boat.setState(0);
                        boat.setAction(2);//如果此时船只剩余空间为零，将状态设为0
                    } else {//如果还有空间
                        //todo:进行装货物操作
                        //先把下面的移过来，后面再讨论这种state属于哪个
                        int currentBerthId = boat.getTargetBerthId();
                        Berth currentBerth = getBerth(currentBerthId, berths);
                        if (boat.getVacancy() > 0) {
                            for (Goods goods : goodsList) {
                                if (!goods.isAssigned() && isInsideBerth(currentBerth, goods)) {
                                    //找到未被分配的货物
                                    boat.load();
                                    goods.setAssigned(true);
                                }
                            }
                        }
                    }

                }
                if (boat.getState() == 2) {//此时船只在泊位外等待进入一个泊位
                    //todo:判断进入哪一个港口
                    Berth bestBerth = findBestBerth(berths, boats);
                    if (bestBerth != null) {
                        boat.setTargetBerthId(bestBerth.getId());
                        boat.setAction(1);
                    }

                }
            }
        }

        public boolean isInsideBerth(Berth berth, Goods goods) {
            return (goods.getPos().X() == berth.getPos().X() || goods.getPos().X() == berth.getPos().X() + 1 || goods.getPos().X() == berth.getPos().X() + 2 || goods.getPos().X() == berth.getPos().X() + 3) && (goods.getPos().Y() == berth.getPos().Y() || goods.getPos().Y() == berth.getPos().Y() + 1 || goods.getPos().Y() == berth.getPos().Y() + 2 || goods.getPos().Y() == berth.getPos().Y() + 3);
        }

        public Berth getBerth(int currentBerthId, Berth[] berths) {
            for (Berth berth : berths) {
                if (berth.getId() == currentBerthId) {
                    return berth;
                }
            }
            return null;
        }

        public Berth findBestBerth(Berth[] berths, Boat[] boats) {
            //找到最优泊位：前提为空，取效率最大的
            Berth bestBerth = null;
            boolean isEmpty = true;
            ArrayList<Berth> emptyBerthList = new ArrayList<>();
            int index = 0;
            for (Berth berth : berths) {
                for (Boat boat : boats) {
                    if (boat.getState() == 1 && boat.getTargetBerthId() == berth.getId()) {
                        isEmpty = false;
                    }
                }
                if (!isEmpty) {
                    continue;
                } else {//确实为空
                    emptyBerthList.add(berth);
                    index++;
                }
            }
            int maxSpeed = Integer.MIN_VALUE;
            if (!emptyBerthList.isEmpty()) {
                for (Berth berth : emptyBerthList) {
                    if (berth.getLoadingSpeed() > maxSpeed) {
                        maxSpeed = berth.getLoadingSpeed();
                        bestBerth = berth;
                    }
                }
            }
            return bestBerth;
        }
    }

    public class randomAssignTarget implements AssignTargetBoat {
        /**
         * 一些参数乱调
         */
        static int berthNo = 0;

        @Override
        public void assign(Frame frame) {
            Boat[] boats = frame.getBoats();
            Berth[] berths = Main.berths;
            int frameNumber = frame.getFrameNumber();
            for (Boat boat : boats) {
                int targetBerthId = boat.getTargetBerthId();
                int state = boat.getState();
                /**
                 * 正在移动中
                 */
                if (state == 0) {
                    //如果最后时刻目标不是-1那就前往-1
                    if (targetBerthId != -1 && !enoughToSell(frameNumber, berths[targetBerthId].getTransportTime())) {
                        boat.setAction(2);
                        continue;
                    }
                    boat.setAction(0);
                    continue;
                } else if (state == 1) {
                    /**
                     * 在虚拟点待机中
                     */
                    if (targetBerthId == -1) {
                        //选最多的泊位去
                        //初始化船只状态为state=1，TargetBerthId=-1
                        //卖完后清空货物
                        boat.clearGoods();
                        boat.setAction(0);
                        assignBerth(frame, boat, berths);
//                        berthNo = (berthNo + 1) % 10;
                    }
                    /**
                     * 在某个泊位待机中
                     */
                    else {
                        //最后时刻全部开往虚拟店
                        Berth berth = berths[targetBerthId];
                        if (!enoughToSell(frameNumber, berth.getTransportTime())) {
                            boat.setAction(2);
                            berth.setAssigned(false);
                            //不让机器人再去此港口
                            //todo:港口关闭还能更早
                            berth.setDeserted();
                        }
                        //装货，泊位的装完就走
                        else if (berth.getGoodsNum() <= 0 || boat.getVacancy() <= 0) {
                            if (boat.getVacancy() <= 0) {
                                boat.setAction(2);
                                berth.setAssigned(false);
                            } else if (frame.getFrameNumber() < (Cons.MAX_FRAME - 550) && boat.getVacancy() > Boat.getCapacity() / 3) {
                                //没到一半去别的港口继续装
                                assignBerth(frame, boat, berths);
                                berth.setAssigned(false);
                                //不加setAssigned意味这个港口也别来了
                                continue;
                            } else {
                                boat.setAction(2);
                                berth.setAssigned(false);
                            }
                        } else {
                            //原地装货
                            boat.setAction(0);
                            loading(boat);
                        }
                    }
                }
                /**
                 * 泊位外等待中
                 */
                else if (boat.getState() == 2) {
                    Berth berth = berths[boat.getTargetBerthId()];
                    if (!enoughToSell(frameNumber, berth.getTransportTime())) {
                        boat.setAction(2);
                        berth.setAssigned(false);
                        //不让机器人再去此港口
                        berth.setDeserted();
                        continue;
                    }
                    else if (berth.getGoodsNum() > Boat.getCapacity() / 2) {
                        //港口货物太多就不走
                        boat.setAction(0);
                        berth.setAssigned(true);
                        continue;
                    }
                    assignBerth(frame, boat, berths);
                }
            }
        }

        private void loading(Boat boat) {
            if (boat.getTargetBerthId() == -1) {
                return;
            }
            Berth berth = Main.berths[boat.getTargetBerthId()];
            int goodsNum = berth.getGoodsNum();
            int loadingSpeed = berth.getLoadingSpeed();
            int loadNum = 0;
            if (berth.getGoodsNum() > 0 && boat.getVacancy() > 0) {
                loadNum = Math.min(boat.getVacancy(), Math.min(loadingSpeed, berth.getGoodsNum()));
                boat.load(loadNum);
                berth.subGoodsNum(loadNum);
            }
        }

        private void assignBerth(Frame frame, Boat boat, Berth[] berths) {
            int maxId = 0;
            double maxWeight = 0;
            for (Berth berth : berths) {
                double weight = Para.boatAssignWeight(boat, berth);
                if (frame.getFrameNumber() > Cons.MAX_FRAME - 3 * Berth.maxTransportTime) {
                    //最终时刻权重不同
                    weight = Para.boatFinalWeight(boat, berth);
                }
                //更换泊位至少需要500+泊位运输时间，还有装货时间
                if (weight > maxWeight && enoughToSell(frame.getFrameNumber(), berth.getTransportTime() + 500 + Boat.getCapacity())) {
                    maxWeight = weight;
                    maxId = berth.getId();
                }
            }
            if (maxId == 0 && berths[0].isItAssigned()) {
                //没分到去流量最大的
                if (frame.getFrameNumber() > Cons.MAX_FRAME - 3 * Berth.maxTransportTime ) {
                    //最后时刻全部运往货流量最大的港口
                    maxId = Para.finalBerthId(frame);
                }else{
                    boat.setAction(0);
                    return;
                }
            }
            if (!enoughToSell(frame.getFrameNumber(), berths[maxId].getTransportTime() + 500 + Boat.getCapacity())){
                //如果运不走就不分配
                return;
            }
            boat.setShipTarget(maxId);
            boat.setAction(1);
            berths[maxId].addFlow(1);
            berths[maxId].setAssigned(true);
        }
    }
    public static boolean enoughToSell(int frameNumber, int transportTime){
        return frameNumber + transportTime < Cons.MAX_FRAME - 5;
    }
}

