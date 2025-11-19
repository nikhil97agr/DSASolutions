package Leetcode;

public class ProcessStringwithSpecialOperationsII {
    char res = ' ';
    public char processStr(String s, long k) {
        process(s, k, 0, 0, s.length());
        return res;
    }


    private long process(String s, long k, long len, int i, int n){

        if(i==n){
            if(k >= len){
                res = '.';
                return -1;
            }
            return k;
        }

        if(s.charAt(i) == '*'){
            return process(s, k, len ==0 ? 0: len-1, i+1, n);

        }else if(s.charAt(i)=='%'){
            long index = process(s, k, len, i+1, n);

            if(index == -1 || res!=' ') return index;

            return len - index - 1;

        }else if(s.charAt(i)=='#'){
            long newLen = len*2;
            long index = process(s, k, newLen, i+1, n);
            if(index == -1 || res!=' ') return index;
            long half = newLen / 2;

            if(index <half) return index;

            return index - half;

        }else{
            long newLen = len + 1;
            long index = process(s, k, newLen, i+1, n);
            if(index == -1 || res!=' ') return index;

            if(index == newLen-1){
                res = s.charAt(i);

            }
            return index;

        }




    }


}