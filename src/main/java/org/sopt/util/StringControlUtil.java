package org.sopt.util;

import java.text.BreakIterator;
import java.util.Locale;

import static java.text.BreakIterator.*;

public class StringControlUtil {

    public static int countCharacters(String input) {
        BreakIterator iterator = getCharacterInstance(Locale.getDefault());
        iterator.setText(input);

        int count = 0;
        int start = iterator.first();
        while (DONE != start){
            int end = iterator.next();
            if (end != DONE){
                count++;
            } else{
                break;
            }
        }
        return count;
    }
}
