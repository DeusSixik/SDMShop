package net.sdm.sdmshopr.api.register;

public interface IRegister<T> {


    String getID();
    default String getModID(){
        return "minecraft";
    }

    T create();
    T copy();
}
