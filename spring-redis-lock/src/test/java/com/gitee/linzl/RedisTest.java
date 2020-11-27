package com.gitee.linzl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@AutoConfigureMockMvc // 如果有servlet则需要添加此注解
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;// 使用这个容易在序列化时出现乱码
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void incr() {
        log.debug("【{}】", "hello");
        // System.out.println("不指定增长步长:" +
        // stringRedisTemplate.opsForValue().increment("helloworld"));
        //
        // System.out.println("增长步长指定为1:" +
        // stringRedisTemplate.opsForValue().increment("test", 1));

        System.out.println("获取自增的值:" + stringRedisTemplate.opsForValue().get("helloworld"));

    }

    /**
     * Redis的字符串是字节序列。在Redis中字符串是二进制安全的，这意味着他们有一个已知的长度，
     * 是没有任何特殊字符终止决定的，所以可以存储任何东西，最大长度可达512兆。
     */
    @Test
    public void storeString() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String key = "helloh";
        ops.set(key, "fodo");
        if (stringRedisTemplate.hasKey(key)) {
            System.out.println("Found key " + key + ", value=" + ops.get(key));
        }
        // 设置过期时间
        stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    /**
     * Redis的哈希键值对的集合。 Redis的哈希值是字符串字段和字符串值之间的映射，所以它们被用来表示对象
     */
    @Test
    public void storeHash() {
        HashOperations<String, Object, Object> ops = stringRedisTemplate.opsForHash();
        String key = "user:1";
        ops.put(key, "name", "fengchao");
        ops.put(key, "sex", "boy");
        if (stringRedisTemplate.hasKey(key)) {
            Set<Object> keys = ops.keys(key);
            for (Object k : keys) {
                System.out.println("key " + key);
            }
        }
        // 设置过期时间
        stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    /**
     * Redis的列表是简单的字符串列表，排序插入顺序。可以添加元素到Redis列表的头部或尾部
     */
    @Test
    public void storeList() {
        ListOperations<String, String> ops = stringRedisTemplate.opsForList();
        String key = "name";
        ops.leftPush(key, "feng");
        ops.leftPush(key, "wang");
        ops.leftPush(key, "li");

        if (stringRedisTemplate.hasKey(key)) {
            Long size = ops.size(key);
            List<String> list = ops.range(key, 0, size);
            for (String value : list) {
                System.out.println(value);
            }
        }
        // 设置过期时间
        stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    /**
     * Redis集合是字符串的无序集合。在Redis中可以添加，删除和测试文件是否存在在O(1)的时间复杂度的成员。
     * <p>
     * 测试计算交并集
     */
    @Test
    public void storeSet() {
        // todo 测试计算交并集
        String key = "likes";

        Set<String> set1 = new HashSet<String>();
        set1.add("set1");
        set1.add("set2");
        set1.add("set3");

        redisTemplate.opsForSet().add(key, set1);

        if (redisTemplate.hasKey(key)) {
            Set<String> members = redisTemplate.opsForSet().members(key);
            for (String value : members) {
                System.out.println(value);
            }
        }
        // 设置过期时间
        redisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    /**
     * Redis的集合排序类似于Redis集合，字符串不重复的集合。
     * <p>
     * 不同的是，一个有序集合的每个成员关联分数，用于以便采取有序set命令，从最小的到最大的分数有关。
     * <p>
     * 虽然成员都是独一无二的，分数可能会重复。
     */
    @Test
    public void storeZList() {
        // todo 测试计算交并集
        ZSetOperations<String, String> ops = stringRedisTemplate.opsForZSet();
        String key = "foods";
        ops.add(key, "test", 4);
        ops.add(key, "friute", 0);
        ops.add(key, "rice", 1);
        ops.add(key, "apple", 1);
        ops.add(key, "balane", 5);

        if (stringRedisTemplate.hasKey(key)) {
            Set<String> sets = ops.rangeByScore(key, 0, 1000);
            for (String value : sets) {
                System.out.println(value);
            }
        }
        // 设置过期时间
        stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    @Test
    public void storeSetBit() {
        // 可以统计用户活跃数，歌曲收藏次数 等等，一天只算一次
        // offset(从左向右数),因为二进制只有0和1，在setbit中true为1，false为0
        // 10个用户
        stringRedisTemplate.opsForValue().setBit("bit", 10, false);
        // id为9的用户登录
        stringRedisTemplate.opsForValue().setBit("bit", 9, true);
        stringRedisTemplate.opsForValue().setBit("bit", 100, true);
        String bit = stringRedisTemplate.opsForValue().get("bit");
        System.out.println("bit==>" + bit);
        BitSet users = BitSet.valueOf(bit.getBytes());
        // 得到1个用户登录
        System.out.println(users.cardinality());
    }

    /**
     * 可运用于订单超时自动关闭
     */
    @Test
    public void storeZList2() {
        // todo 测试计算交并集
        ZSetOperations<String, String> ops = stringRedisTemplate.opsForZSet();
        ops.add("orderExpire", "订单号1111", System.currentTimeMillis());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ops.add("orderExpire", "订单号2222", System.currentTimeMillis());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ops.add("orderExpire", "订单号3333", System.currentTimeMillis());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 5秒之前的数据
        long max = System.currentTimeMillis() - 5 * 1000;
        Set<TypedTuple<String>> rangeWithScores = ops.rangeByScoreWithScores("orderExpire", 0, max);
        Iterator<TypedTuple<String>> iterator = rangeWithScores.iterator();
        while (iterator.hasNext()) {
            TypedTuple<String> next = iterator.next();
            double d = next.getScore();
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            String time = nf.format(d);
            Long recordTime = Long.parseLong(time);
            String value = next.getValue();
            System.out.println("value:" + value + ",time=" + time + ",recordTime=" + recordTime);
            long deletCnt = ops.remove("orderExpire", value);
            System.out.println("deletCnt==>" + deletCnt);
        }
    }

    @Test
    public void testTwoLock() {
        RLock first = redissonClient.getLock("1122");
        first.lock();

        RLock second = redissonClient.getLock("2233");
        second.lock();

        System.out.println("hello11");
        System.out.println("hello22");

        first.unlock();
        second.unlock();
    }

    @Test
    public void testHyperloglog() {
        stringRedisTemplate.opsForHyperLogLog().add("hello", "1", "2", "3", "4", "5", "6");
        stringRedisTemplate.opsForGeo().add("kkk1", new Point(1, 2), "商家1");
        stringRedisTemplate.opsForGeo().add("kkk1", new Point(2, 3), "商家2");
        Distance distance = stringRedisTemplate.opsForGeo().distance("kkk1", "商家1", "商家2");
        System.out.println(distance);
    }

    @Test
    public void GeoRadiusByMember() {
        long startTime = System.currentTimeMillis(); // 获取开始时间

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        // 设置geo查询参数
        GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
        geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();// 查询返回结果包括距离和坐标
        geoRadiusArgs.sortAscending();// 按查询出的坐标距离中心坐标的距离进行排序
        geoRadiusArgs.limit(100);// 限制查询数量

        GeoResults<GeoLocation<String>> radiusGeo = geoOps.radius("kkk2", "user1",
                new Distance(10, DistanceUnit.KILOMETERS), geoRadiusArgs);
        radiusGeo = geoOps.radius("kkk2",
                new Circle(new Point(120.1384162903, 30.2532102251), new Distance(5, DistanceUnit.KILOMETERS)),
                geoRadiusArgs);

        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); // 输出程序运行时间

        List<GeoResult<GeoLocation<String>>> content = radiusGeo.getContent();
        for (GeoResult<GeoLocation<String>> geoLocationGeoResult : content) {
            System.out.println(geoLocationGeoResult.getContent());
        }
    }

    @Test
    public void zReverseRange() {
        Long showSize = 100L;
        Long size = stringRedisTemplate.opsForZSet().size("zReverseRange1");
        Long start = 0L;
        if (size > 0) {
            if (size >= showSize) {
                start = size - showSize;
            }
        }
        System.out.println("排行榜数:" + size);
        System.out.println("排行榜数start:" + start);
        if (showSize > 0) {

        }
        Set<String> rangeWithScores = stringRedisTemplate.opsForZSet().reverseRange("zReverseRange", 0, showSize - 1);
        System.out.println("rangeWithScores==>"+rangeWithScores);
        Iterator<String> iterator = rangeWithScores.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            System.out.println("next:" + next);
        }
        if (start > 0) {
            stringRedisTemplate.opsForZSet().removeRange("zReverseRange", 0, start - 1);
        }
    }

    @Test
    public void testList() {
        redisTemplate.opsForValue().set("age:hello",new Student(11,"是我"));
        redisTemplate.opsForValue().set("age:hello1","是我");

        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        String key = "age:hello2";
        ops.put(key, "name", "是我");
        ops.put(key, "sex", "boy");
    }

    @Data
    private static class Student {
        private Integer age;
        private String name;

        public Student(Integer age, String name) {
            this.age = age;
            this.name = name;

        }
    }
}
