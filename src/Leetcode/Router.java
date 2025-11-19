package Leetcode;

import java.util.*;

public class Router {
    int limit;
    Queue<Packet> que;
    Set<Packet> set;

    Map<Integer, List<Integer>> map;

    public Router(int memoryLimit) {
        limit = memoryLimit;
        que = new LinkedList<>();
        set = new HashSet<>();
        map = new HashMap<>();
    }

    public boolean addPacket(int source, int destination, int timestamp) {
        Packet packet = new Packet(source, destination, timestamp);
        if(set.contains(packet)) return false;
        if(que.size() == limit){
            Packet p = que.poll();
            remove(p.time, p.dest);
        }

        que.offer(packet);
        set.add(packet);
        add(packet.time, packet.dest);

        return true;

    }

    public int[] forwardPacket() {
        if(que.isEmpty()) return new int[0];

        Packet packet = que.poll();

        remove(packet.time, packet.dest);

        return packet.value();

    }

    private void remove(int time, int destination){
        List<Integer> list = map.get(destination);
        int ind = bs(list, time);

        list.remove(ind);

        if(list.isEmpty()){
            map.remove(destination);
        }
    }

    private void add(int time, int destination){
        List<Integer> list = map.computeIfAbsent(destination, d -> new ArrayList<>());

        int ind = bs(list, time);
        if(ind == -1){
            list.add(time);
        }else{
            list.add(ind, time);
        }
    }

    private int bs(List<Integer> list, int val){
        int start = 0;
        int end = list.size()-1;
        int ind = -1;

        while(start <= end){
            int mid = (start+end)/2;
            int v = list.get(mid);

            if(val <= v){
                ind = mid;
                end = mid-1;
            }else{
                start = mid +1;
            }
        }
        return ind;
    }
    public int getCount(int destination, int startTime, int endTime) {
        if(!map.containsKey(destination)) return 0;

        List<Integer> list = map.get(destination);

        int start = bs(list, startTime);
        int end = bs(list,endTime+1);

        if(start == -1){
            return 0;
        }

        if(end == -1){
            end = list.size();
        }

        return end - start;

    }

    class Packet{
        int src;
        int dest;
        int time;

        public Packet(int src, int dest, int time) {
            this.src = src;
            this.dest = dest;
            this.time = time;
        }

        public int[] value(){
            return new int[]{
                    src, dest, time
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Packet packet = (Packet) o;
            return src == packet.src && dest == packet.dest && time == packet.time;
        }

        @Override
        public int hashCode() {
            return Objects.hash(src, dest, time);
        }
    }
}

/**
 * Your Router object will be instantiated and called as such:
 * Router obj = new Router(memoryLimit);
 * boolean param_1 = obj.addPacket(source,destination,timestamp);
 * int[] param_2 = obj.forwardPacket();
 * int param_3 = obj.getCount(destination,startTime,endTime);
 */