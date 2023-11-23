package Leetcode;

// Problem Link : https://leetcode.com/problems/excel-sheet-column-title

public class ExcelSheetColumnTitle {

    public String convertToTitle(int n) {
        StringBuilder result = new StringBuilder();
        while (n > 0) {
            result.insert(0, (char) ('A' + (n - 1) % 26));
            /*
             * Que : Why (n-1)?
             * Ans : Lets say n=1 then if we do n%26 it will return 1 which will be added to
             * 'A'
             * because of which for n=1 result should be 'A' but it will return 'B'. That's
             * why (n-1) is done
             * to prevent them with index position.
             */
            n = (n - 1) / 26;
        }
        return result.toString();

    }

}
