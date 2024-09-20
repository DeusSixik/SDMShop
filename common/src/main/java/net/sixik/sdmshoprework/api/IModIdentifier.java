package net.sixik.sdmshoprework.api;

public interface IModIdentifier extends IIdentifier {

    default String getModId() {
        return "minecraft";
    }
}
