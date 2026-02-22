package com.minecraft.utils;

public class ConsoleUtils {
    private static final String ASCII_ART =
        "     ███╗   ███╗██╗███╗   ██╗███████╗ ██████╗██████╗  █████╗ ███████╗████████╗ \n" +
        "    ████╗ ████║██║████╗  ██║██╔════╝██╔════╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝  \n" +
        "    ██╔████╔██║██║██╔██╗ ██║█████╗  ██║     ██████╔╝███████║█████╗     ██║     \n" +
        "    ██║╚██╔╝██║██║██║╚██╗██║██╔══╝  ██║     ██╔══██╗██╔══██║██╔══╝     ██║     \n" +
        "    ██║ ╚═╝ ██║██║██║ ╚████║███████╗╚██████╗██║  ██║██║  ██║██║        ██║     \n" +
        "    ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝     \n" ;

    public static void printStartupMessages() {
        printAsciiArt(ASCII_ART);
        printSuccessMessage("");
        printUrl("","");
    }
    
    public static void printAsciiArt(String art) {
        System.out.println(art);
    }
    
    public static void printSuccessMessage(String message) {
        System.out.println("🚀" + "启动成功！" + "🚀");
    }
    
    public static void printUrl(String name, String url) {
        System.out.println("🌍" + "后端" + "地址:" + "http://localhost:8080/");
        System.out.println("📃"+"接口文档"+"地址:"+"http://localhost:8080/apiDocument.html");
        System.out.println("🌌" + "前端" + "地址:" + "http://localhost:8090/");
        System.out.println("🧑‍🤝‍🧑"+"即时通讯" + "地址:" + "http://localhost:8080/im-api-documentation.html");
        System.out.println("🧺"+"Github地址:" + "https://github.com/HH-han");
        System.out.println("🐵"+"程序猿:"+"bolan");
        System.out.println("========================================");
        System.out.println("     Minecraft Backend Started Successfully!");
        System.out.println("    - Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("    - OpenAPI JSON: http://localhost:8080/v3/api-docs");
        System.out.println("========================================");
        System.out.println("旅游推荐订票系统启动成功！");
    }
}