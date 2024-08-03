package com.ispengya.shortlink.project.util;

import cn.hutool.core.lang.hash.MurmurHash;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HASH 工具类
 */
public class HashUtil {

    public static final Map<Integer, Set<Integer>> map=new HashMap<>();

    static {
        map.put(0, Set.of(0,1,2));
        map.put(1, Set.of(3,4,5));
    }

    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final int SIZE = CHARS.length;

    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    public static String hashToBase62(String str) {
        int i = MurmurHash.hash32(str);
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        return convertDecToBase62(num);
    }

    public static int hashAndMapToRange(String s,int shardRange,String databaseSuffix){
        int hashNum = Math.abs(MurmurHash.hash32(s));
        int tableIndex = hashNum%shardRange;
        if (!isShardingTrue(tableIndex,databaseSuffix)){
            if (databaseSuffix.equals("0")){
                tableIndex=tableIndex-3;
            }else if (databaseSuffix.equals("1")){
                tableIndex=tableIndex+3;
            }
            return tableIndex;
        }
        return tableIndex;
    }

    public static boolean isShardingTrue(int tableIndex, String databaseSuffix) {
        Set<Integer> integers = map.get(Integer.parseInt(databaseSuffix));
        return integers.contains(tableIndex);
    }

    public static void main(String[] args) {
        System.out.println(isShardingTrue(5, "1"));
    }
}