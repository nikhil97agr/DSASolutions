package Leetcode;

/**
 * Problem Link: https://leetcode.com/problems/find-the-k-th-character-in-string-game-ii/
 */

public class KthCharacterInStringGameII {
    public static void main(String[] args) {
        new KthCharacterInStringGameII().kthCharacter(10, new int[]{0,1,0,1});
    }
    public char kthCharacter(long k, int[] operations) {
        Response response = find(k, operations, 0, 1, 0);
        char curr = 'a';
        int steps= response.steps;
        for(int i=0;i<steps;i++){
            if(curr == 'z') curr = 'a';
            else curr++;
        }

        return curr;
    }

    private Response find(long k,int op[], int i, long len, int prev){
        if(len >= k){
            long pos = k - len/2;
            return new Response(pos, prev == 1 ? 1 : 0);
        }

        Response response = find(k, op, i+1, len*2, op[i]);

        int step = response.steps;
        long pos = response.pos;

        if(pos > len / 2){
            pos = pos - len/2;

            return new Response(pos, step + (prev==1 ? 1 : 0));
        }else{
            return new Response(pos, step );
        }
    }

    class Response{
        long pos;
        int steps;

        public Response(long pos, int steps){
            this.pos = pos;
            this.steps = steps;
        }
    }
}