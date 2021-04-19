package com.bcu.aster.printer.printerstation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Box {
    Map<Integer,String> map=new HashMap();
    public Box(){
        map.put(0,null);
        map.put(1,null);
        map.put(2,null);
        map.put(3,null);
        map.put(4,null);

    }

    public Integer getDoorIdByCode(String code){
        for(Integer i:map.keySet()){
            if (map.get(i).equals(code)) return i;
        }
        return -1;
    }

    public Integer setDoorIdByCode(String code){
        Integer integer = hasFreeDoor();if(integer==-1) return -1;
        map.put(integer,code);
        return integer;
    }

    public Integer hasFreeDoor(){
        for(Integer i:map.keySet()){
            if (map.get(i)==null) return i;
        }
        return -1;
    }


}
