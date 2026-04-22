package com.example.init; // Убедитесь, что пакет совпадает с вашим

public class Reference {
    public static final String MODID = "samplemod112";
    public static final String NAME = "Sample Mod 1.12";
    public static final String VERSION = "1.0";

    // Здесь тоже указываем пути к вашим прокси
    // (Если вы переименовали ClineProxy в ClientProxy, исправьте тут слово Cline на Client)
    public static final String CLIENT_PROXY_CLASS = "com.example.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.example.proxy.CommonProxy";
}