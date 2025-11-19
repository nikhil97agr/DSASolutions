package Leetcode;

import java.util.*;

public class FindServersThatHandledMostNumberOfRequests{

    public List<Integer> busiestServers(int k, int[] arrival, int[] load) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < k; i++) set.add(i);

        Map<Integer, Integer> serverLoad = new HashMap<>();
        Map<Integer, Integer> requests = new HashMap<>();
        PriorityQueue<Request> que = new PriorityQueue<>((r1, r2) -> {
            if (r1.time == r2.time) {
                if(r1.flag == r2.flag){
                    return r1.id - r2.id;
                }
                return r1.flag - r2.flag;
            }

            return r1.time - r2.time;
        });

        int n = arrival.length;

        for (int i = 0; i < n; i++) {
            que.offer(new Request(i, arrival[i], 1));
        }

        while (!que.isEmpty()) {
            Request request = que.poll();

            if (request.flag == 1) {
                if (!set.isEmpty()) {
                    int req = request.id%k;
                    if(!set.contains(req)){
                        Integer max = set.higher(req);
                        if(max !=null){
                            req = max;
                            set.remove(max);
                        }else{
                            req = set.pollFirst();
                        }
                    }else{
                        set.remove(req);
                    }
                    requests.put(request.id, req);
                    serverLoad.merge(req, 1, Integer::sum);
                    que.offer(new Request(request.id, request.time + load[request.id], -1));
                }
            } else {
                int server = requests.get(request.id);
                set.add(server);
                requests.remove(request.id);
            }
        }
        final int max = serverLoad.entrySet().stream().map(e -> e.getValue()).max(Integer::compareTo).get();

        return serverLoad.entrySet().stream().filter( e-> e.getValue() == max).map(Map.Entry::getKey).toList();
    }

    class Request {
        int id;
        int time;
        int flag;

        public Request(int id, int time, int flag) {
            this.id = id;
            this.time = time;
            this.flag = flag;
        }
    }
}