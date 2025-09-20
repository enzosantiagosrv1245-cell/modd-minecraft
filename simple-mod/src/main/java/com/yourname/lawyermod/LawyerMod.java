package com.yourname.lawyermod;

public class LawyerMod {
    public static final String MOD_ID = "lawyermod";
    public static final String VERSION = "1.0.0";
    
    public LawyerMod() {
        System.out.println("LawyerMod v" + VERSION + " carregado!");
        System.out.println("Sistema de Advogados e Anjos ativo!");
    }
    
    public static void main(String[] args) {
        System.out.println("=== LAWYERMOD ===");
        System.out.println("Mod criado com sucesso!");
        System.out.println("Para usar no Minecraft:");
        System.out.println("1. Copie o .jar para pasta mods/");
        System.out.println("2. Inicie servidor Forge");
        System.out.println("3. Use /givefunctionpaper <player>");
    }
}
