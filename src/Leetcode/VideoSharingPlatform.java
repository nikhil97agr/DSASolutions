package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class VideoSharingPlatform {
    Map<Integer, Video> map;
    int nextId = 0;
    PriorityQueue<Integer> smallId;

    public VideoSharingPlatform() {
        map = new HashMap<>();
        smallId= new PriorityQueue<>();
    }
    
    public int upload(String video) {
        if(!smallId.isEmpty()){
            int id = smallId.poll();

            map.put(id, new Video(id, video));
            return id;
        }
        map.put(nextId, new Video(nextId, video));
        nextId++;

        return nextId -1;


    }
    
    public void remove(int videoId) {
        if(!map.containsKey(videoId)) return;
        map.remove(videoId);
        smallId.offer(videoId);
    }
    
    public String watch(int videoId, int startMinute, int endMinute) {
        if(!map.containsKey(videoId)) return "-1";

        Video video = map.get(videoId);

        video.view++;
        int len = video.video.length();
        return video.video.substring(startMinute, Math.min(endMinute + 1, len));
    }
    
    public void like(int videoId) {
        if(map.containsKey(videoId)){
            map.get(videoId).like++;
        }
    }
    
    public void dislike(int videoId) {
        if(map.containsKey(videoId)){
            map.get(videoId).dislike++;
        }
    }
    
    public int[] getLikesAndDislikes(int videoId) {
        if(map.containsKey(videoId)){
            Video video = map.get(videoId);

            return new int[]{video.like, video.dislike};
        }

        return new int[]{-1};
    }
    
    public int getViews(int videoId) {
        if(map.containsKey(videoId)){
            return map.get(videoId).view;
        }

        return -1;
    }

    class Video{
        String video;
        int id;
        int like;
        int dislike;
        int view;
        public Video(int id, String video){
            this.id = id;
            this.video =video;
        }
    }
}

/**
 * Your VideoSharingPlatform object will be instantiated and called as such:
 * VideoSharingPlatform obj = new VideoSharingPlatform();
 * int param_1 = obj.upload(video);
 * obj.remove(videoId);
 * String param_3 = obj.watch(videoId,startMinute,endMinute);
 * obj.like(videoId);
 * obj.dislike(videoId);
 * int[] param_6 = obj.getLikesAndDislikes(videoId);
 * int param_7 = obj.getViews(videoId);
 */