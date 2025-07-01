package net.sixik.sdmshop.utils;

import java.util.List;

public record RemoveResult(boolean success, List<Integer> removedIndices) {
}
