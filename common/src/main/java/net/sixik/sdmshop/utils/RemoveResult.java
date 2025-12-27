package net.sixik.sdmshop.utils;

import java.util.List;

public record RemoveResult(boolean success, List<Integer> removedIndices) {

    public RemoveResult(boolean success) {
        this(success, DUMMY);
    }

    private static final List<Integer> DUMMY = List.of();

    public static final RemoveResult FAIL = new RemoveResult(false, DUMMY);

    public static final RemoveResult SUCCESS = new RemoveResult(true, DUMMY);
}
