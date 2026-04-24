//package org.example.misc;
//
///**
// * Utilidad de consola para limpiar pantalla sin romper el flujo si falla el comando nativo.
// */
//public final class ConsolaUtils {
//
//    private ConsolaUtils() {
//    }
//
//    public static void limpiarPantalla() {
//        // 1) Intento ANSI (funciona bien en muchas consolas, incluido terminal embebido).
//        try {
//            System.out.print("\033[H\033[2J");
//            System.out.flush();
//            return;
//        } catch (Exception ignored) {
//            // Continúa con fallback.
//        }
//
//        // 2) Intento comando nativo.
//        try {
//            String os = System.getProperty("os.name", "").toLowerCase();
//            ProcessBuilder pb;
//            if (os.contains("win")) {
//                pb = new ProcessBuilder("cmd", "/c", "cls");
//            } else {
//                pb = new ProcessBuilder("clear");
//            }
//            pb.inheritIO().start().waitFor();
//            return;
//        } catch (Exception ignored) {
//            // Continúa con fallback final.
//        }
//
//        // 3) Fallback final.
//        for (int i = 0; i < 40; i++) {
//            System.out.println();
//        }
//    }
//}
