package org.sopt.util;

import com.ibm.icu.util.ULocale;

import java.util.Locale;
import com.ibm.icu.text.*;


public class StringControlUtil {

    public static int countCharacters(String input) {
        BreakIterator iterator = BreakIterator.getCharacterInstance(ULocale.getDefault());

        iterator.setText(input);

        int count = 0;
        int start = iterator.first();
        while (start != BreakIterator.DONE) {
            int end = iterator.next();
            if (end == BreakIterator.DONE) break;
            count++;
        }
        return count;
    }
}
