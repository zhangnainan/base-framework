package com.sg.base.model.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局枚举
 *
 * @author Dai Wenqing
 * @date 2016/10/10
 */
public class Global {
    /**
     * 性别
     */
    public enum SexType {
        Man(0),
        Woman(1);

        private int type;

        SexType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        private Map<Integer, SexType> map;

        public SexType get(int type) {
            if (map == null) {
                map = new ConcurrentHashMap<>();
                for (SexType dataType : SexType.values())
                    map.put(dataType.getType(), dataType);
            }
            return map.get(type);
        }
    }

    /**
     * 有效标记
     */
    public enum ValidFlag {
        /**
         * 彻底删除
         */
        Clear(-1),
        /**
         * 有效
         */
        Valid(1),
        /**
         * 已删除
         */
        Delete(0);
        private int type;

        ValidFlag(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        private Map<Integer, ValidFlag> map;

        public ValidFlag get(int type) {
            if (map == null) {
                map = new ConcurrentHashMap<>();
                for (ValidFlag dataType : ValidFlag.values())
                    map.put(dataType.getType(), dataType);
            }
            return map.get(type);
        }
    }

    /**
     * -1 = 只查询无效的数据,0=只查询有效数据,1=查询全部数据
     */
    public enum QueryFlag {
        Invalid(-1),
        Valid(0),
        All(1);
        private int type;

        QueryFlag(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        private Map<Integer, QueryFlag> map;

        public QueryFlag get(int type) {
            if (map == null) {
                map = new ConcurrentHashMap<>();
                for (QueryFlag dataType : QueryFlag.values())
                    map.put(dataType.getType(), dataType);
            }
            return map.get(type);
        }
    }

}
