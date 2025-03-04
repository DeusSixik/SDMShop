package net.sixik.sdmshoprework.api;

public class SDMSerializeParam {

    public static final int SERIALIZE_PARAMS = 1;
    public static final int SERIALIZE_LIMIT = 2;
    public static final int SERIALIZE_CONDITIONS = 3;
    /**
     * Only on ShopTab or ShopBase
     */
    public static final int SERIALIZE_ENTRIES = 4;

    public static final int SERIALIZE_WITHOUT_ENTRIES = SERIALIZE_PARAMS | SERIALIZE_CONDITIONS | SERIALIZE_LIMIT;
    public static final int SERIALIZE_ALL = SERIALIZE_PARAMS | SERIALIZE_CONDITIONS | SERIALIZE_ENTRIES | SERIALIZE_LIMIT;
}
