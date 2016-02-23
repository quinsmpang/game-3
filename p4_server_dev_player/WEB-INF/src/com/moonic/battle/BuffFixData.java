package com.moonic.battle;

public class BuffFixData {
	public int num; //buff编号
    public String name; //buff名
    public short[][] buffTypes; //buff类型
    public short buffGroup;  //分组
    public byte updown;  //1增加 2减少
    public byte goodbad;  //增益减益
    public boolean cleanable; //可净化
    public byte turns;  //持续回合数
    public byte limitTimes;  //生效次数限制    
    public int[][] args; //参数    
    public byte priority;  //优先级
    
}
