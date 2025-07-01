package net.sixik.sdmshop.utils;

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

    public static <T> void swap(List<T> list, int from, int to) {
        Collections.swap(list, from, to);
    }

    public static <T> void insert(List<T> list, int from, int to) {
        if (from < 0 || to < 0 || from >= list.size() || to > list.size()) {
            return;
        }

        if (from == to || from == to - 1) {
            return;
        }

        T element = list.remove(from);
        if (to > from) {
            list.add(to - 1, element);
        } else {
            list.add(to, element);
        }
    }
}
