package test;

import com.huawei.codecraft.Cons;
import com.huawei.codecraft.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class UtilsTest {
    @Test
    public void testMapInit(){
        Map map = MapUtils.mapInit();
        char[][] mapData = map.getMapData();
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            for (int j = 0; j < Cons.MAP_SIZE; j++) {
                Assert.assertEquals('.', mapData[i][j]);
            }
        }
    }
}
