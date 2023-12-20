package Leetcode;


public class Minesweeper {
    public char[][] updateBoard(char[][] board, int[] click) {
        int m = board.length;
        int n = board[0].length;
        char result[][] = board.clone();
        int r = click[0];
        int c = click[1];
        if(board[r][c] == 'M') {
            board[r][c] = 'X';
            return board;
        }
        reveal(r, c, m, n, board);
        return board;
    }

   private void reveal(int r, int c, int m, int n, char result[][]){
        if(r <0 || c < 0 || r >= m || c >= n  || result[r][c] !='E') return;

        int count = countMines(result, r, c, m, n);
        if(count > 0){
            result[r][c] = (char)('0'+count);
            return;
        }
        result[r][c] = 'B';
       int x[] = new int[]{1,1,1,0,-1,-1,-1,0};
       int y[] = new int[]{1,0,-1,-1,-1,0,1,1};

       for(int i=0;i<8;i++){
           reveal(r+x[i], c+y[i], m, n, result);
       }
   }

   private int countMines(char board[][], int r, int c, int m, int n){
        int x[] = new int[]{1,1,1,0,-1,-1,-1,0};
        int y[] = new int[]{1,0,-1,-1,-1,0,1,1};

        int cnt = 0;
        for(int i=0;i<8;i++){
            int nr = r+x[i];
            int nc = c + y[i];
            if(nr >=0 && nc >=0 && nr <m && nc < n && board[nr][nc] == 'M') cnt++;
        }

        return cnt;
   }
}


