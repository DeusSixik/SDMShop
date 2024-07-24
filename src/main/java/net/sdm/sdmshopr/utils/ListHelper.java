package net.sdm.sdmshopr.utils;

import java.util.Collections;
import java.util.List;

public class ListHelper {

    public static <T> void moveUp(List<T> list, int index) {
        if (index > 0 && index < list.size()) {
            Collections.swap(list, index, index - 1);
        }
    }

    public static <T> void moveDown(List<T> list, int index) {
        if (index >= 0 && index < list.size() - 1) {
            Collections.swap(list, index, index + 1);
        }
    }
}
