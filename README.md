# sql-util
支持动态拼接sql，有问题可以留言，我会处理

使用方式：

```java
public class MainTest {

    public static void main(String[] args) {
        List<Integer> city_id = new ArrayList<>();
        city_id.add(1);
        city_id.add(2);
        
        SqlCreateUtil sqlCreateUtil = new SqlCreateUtil();
        sqlCreateUtil.table("ucore_db_order")
                .fields("waiting_fee_fen,order_display_id")
                .field("ep_id")
                .andCondition("order_vehicle_id", RelationalOperators.EQ,1)
                .orCondition("order_status",RelationalOperators.EQ,2)
                .andConditionNotIn("city_id",city_id)
                .orConditionIn("driver_id",city_id)
                .limit(1,5)
                .andLike("remark","heiehieh")
                .orderBy(SortMethod.DESC,"order_id");

        System.out.println(sqlCreateUtil.toString());

    }
}

```

