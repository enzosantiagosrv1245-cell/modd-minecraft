package com.yourname.lawyermod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModCommands {

    public static void register() {
        // Os comandos são registrados no evento RegisterCommandsEvent
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Comando para selecionar função
        dispatcher.register(Commands.literal("selectfunction")
                .then(Commands.argument("function", StringArgumentType.string())
                        .executes(ModCommands::selectFunction)));

        // Comando para julgar
        dispatcher.register(Commands.literal("julgar")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::judgePlayer)));

        // Comando para libertar
        dispatcher.register(Commands.literal("liberto")
                .executes(ModCommands::freePlayer));

        // Comando para condenar
        dispatcher.register(Commands.literal("condenado")
                .executes(ModCommands::condemnPlayer));

        // Comando para dar papel de função (admin)
        dispatcher.register(Commands.literal("givefunctionpaper")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::giveFunctionPaper)));

        // Comando para remover função (admin)
        dispatcher.register(Commands.literal("removefunction")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::removeFunction)));

        // Comando para ver missão
        dispatcher.register(Commands.literal("mission")
                .executes(ModCommands::showMission));

        // Comandos de anjo
        dispatcher.register(Commands.literal("heal")
                .executes(ModCommands::angelHeal));

        dispatcher.register(Commands.literal("fly")
                .executes(ModCommands::angelFly));

        dispatcher.register(Commands.literal("bless")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::angelBless)));
    }

    private static int selectFunction(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            String function = StringArgumentType.getString(context, "function");
            
            if (!PlayerDataManager.isSelectingFunction(player)) {
                player.sendSystemMessage(Component.literal("§cVocê precisa usar o papel de função primeiro!"));
                return 0;
            }

            if (function.equalsIgnoreCase("lawyer")) {
                PlayerDataManager.setPlayerFunction(player, "lawyer");
                PlayerDataManager.setSelectingFunction(player, false);
                player.sendSystemMessage(Component.literal("§aVocê se tornou um Advogado!"));
                player.sendSystemMessage(Component.literal("§eComandos: /julgar <player>, /liberto, /condenado"));
                player.sendSystemMessage(Component.literal("§eUse /mission para ver sua missão!"));
                return 1;
            } else if (function.equalsIgnoreCase("angel")) {
                PlayerDataManager.setPlayerFunction(player, "angel");
                PlayerDataManager.setSelectingFunction(player, false);
                player.sendSystemMessage(Component.literal("§bVocê se tornou um Anjo!"));
                player.sendSystemMessage(Component.literal("§eBenefícios: Imortalidade, Voo, Cura"));
                player.sendSystemMessage(Component.literal("§eComandos: /heal, /fly, /bless <player>"));
                player.sendSystemMessage(Component.literal("§eUse /mission para ver sua missão!"));
                return 1;
            } else {
                player.sendSystemMessage(Component.literal("§cFunção não encontrada! Use: lawyer ou angel"));
            }
        }
        return 0;
    }

    private static int judgePlayer(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer lawyer) {
            if (!PlayerDataManager.hasFunction(lawyer, "lawyer")) {
                lawyer.sendSystemMessage(Component.literal("§cVocê não é um advogado!"));
                return 0;
            }

            try {
                ServerPlayer target = EntityArgument.getPlayer(context, "player");
                
                if (target.equals(lawyer)) {
                    lawyer.sendSystemMessage(Component.literal("§cVocê não pode julgar a si mesmo!"));
                    return 0;
                }

                // Teletransportar o julgado para frente do advogado
                BlockPos lawyerPos = lawyer.blockPosition();
                target.teleportTo(lawyerPos.getX() + 2, lawyerPos.getY(), lawyerPos.getZ());
                
                // Marcar como julgado
                PlayerDataManager.setJudgedBy(target, lawyer);
                
                lawyer.sendSystemMessage(Component.literal("§aVocê está julgando " + target.getName().getString()));
                target.sendSystemMessage(Component.literal("§cVocê está sendo julgado por " + lawyer.getName().getString()));
                target.sendSystemMessage(Component.literal("§cVocê não pode se mover até o veredicto!"));
                
                return 1;
            } catch (Exception e) {
                lawyer.sendSystemMessage(Component.literal("§cJogador não encontrado!"));
            }
        }
        return 0;
    }

    private static int freePlayer(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer lawyer) {
            if (!PlayerDataManager.hasFunction(lawyer, "lawyer")) {
                lawyer.sendSystemMessage(Component.literal("§cVocê não é um advogado!"));
                return 0;
            }

            // Encontrar jogador julgado por este advogado
            ServerPlayer judgedPlayer = findJudgedPlayer(lawyer);
            if (judgedPlayer != null) {
                // Libertar e remover metade da vida
                float currentHealth = judgedPlayer.getHealth();
                judgedPlayer.setHealth(currentHealth / 2);
                
                PlayerDataManager.freePlayer(judgedPlayer);
                
                lawyer.sendSystemMessage(Component.literal("§aVocê libertou " + judgedPlayer.getName().getString()));
                judgedPlayer.sendSystemMessage(Component.literal("§aVocê foi libertado, mas perdeu metade da sua vida!"));
                
                return 1;
            } else {
                lawyer.sendSystemMessage(Component.literal("§cVocê não está julgando ninguém!"));
            }
        }
        return 0;
    }

    private static int condemnPlayer(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer lawyer) {
            if (!PlayerDataManager.hasFunction(lawyer, "lawyer")) {
                lawyer.sendSystemMessage(Component.literal("§cVocê não é um advogado!"));
                return 0;
            }

            ServerPlayer judgedPlayer = findJudgedPlayer(lawyer);
            if (judgedPlayer != null) {
                // Teletransportar para local aleatório (prisão)
                Level world = judgedPlayer.getLevel();
                BlockPos randomPos = new BlockPos(
                    (int) (Math.random() * 2000 - 1000),
                    70,
                    (int) (Math.random() * 2000 - 1000)
                );
                
                judgedPlayer.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
                PlayerDataManager.freePlayer(judgedPlayer);
                
                lawyer.sendSystemMessage(Component.literal("§cVocê condenou " + judgedPlayer.getName().getString()));
                judgedPlayer.sendSystemMessage(Component.literal("§cVocê foi condenado e enviado para um local distante!"));
                
                return 1;
            } else {
                lawyer.sendSystemMessage(Component.literal("§cVocê não está julgando ninguém!"));
            }
        }
        return 0;
    }

    private static int giveFunctionPaper(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "player");
            target.getInventory().add(ModItems.FUNCTION_PAPER.get().getDefaultInstance());
            
            context.getSource().sendSuccess(Component.literal("Papel de função dado para " + target.getName().getString()), true);
            target.sendSystemMessage(Component.literal("§aVocê recebeu um papel de função!"));
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Jogador não encontrado!"));
        }
        return 0;
    }

    private static int removeFunction(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "player");
            String currentFunction = PlayerDataManager.getPlayerFunction(target);
            
            if (currentFunction != null) {
                PlayerDataManager.removePlayerFunction(target);
                context.getSource().sendSuccess(Component.literal("Função removida de " + target.getName().getString()), true);
                target.sendSystemMessage(Component.literal("§cSua função foi removida por um administrador!"));
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("Jogador não possui função!"));
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Jogador não encontrado!"));
        }
        return 0;
    }

    private static int showMission(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            MissionSystem.showMissionStatus(player);
            return 1;
        }
        return 0;
    }

    // Comandos de Anjo
    private static int angelHeal(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            if (!PlayerDataManager.hasFunction(player, "angel")) {
                player.sendSystemMessage(Component.literal("§cApenas anjos podem usar este comando!"));
                return 0;
            }

            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);
            player.sendSystemMessage(Component.literal("§bVocê se curou completamente com poderes angelicais!"));
            return 1;
        }
        return 0;
    }

    private static int angelFly(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            if (!PlayerDataManager.hasFunction(player, "angel")) {
                player.sendSystemMessage(Component.literal("§cApenas anjos podem usar este comando!"));
                return 0;
            }

            boolean canFly = player.getAbilities().mayfly;
            player.getAbilities().mayfly = !canFly;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();

            if (player.getAbilities().mayfly) {
                player.sendSystemMessage(Component.literal("§bVoo angelical ativado! Use espaço para voar."));
            } else {
                player.sendSystemMessage(Component.literal("§7Voo angelical desativado."));
            }
            return 1;
        }
        return 0;
    }

    private static int angelBless(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer angel) {
            if (!PlayerDataManager.hasFunction(angel, "angel")) {
                angel.sendSystemMessage(Component.literal("§cApenas anjos podem usar este comando!"));
                return 0;
            }

            try {
                ServerPlayer target = EntityArgument.getPlayer(context, "player");
                
                // Curar completamente o jogador alvo
                target.setHealth(target.getMaxHealth());
                target.getFoodData().setFoodLevel(20);
                
                // Dar efeitos positivos temporários (simulados via mensagens)
                angel.sendSystemMessage(Component.literal("§bVocê abençoou " + target.getName().getString() + "!"));
                target.sendSystemMessage(Component.literal("§bVocê foi abençoado por " + angel.getName().getString() + "!"));
                target.sendSystemMessage(Component.literal("§eSua vida e fome foram restauradas!"));
                
                return 1;
            } catch (Exception e) {
                angel.sendSystemMessage(Component.literal("§cJogador não encontrado!"));
            }
        }
        return 0;
    }

    private static ServerPlayer findJudgedPlayer(ServerPlayer lawyer) {
        for (ServerPlayer player : lawyer.getServer().getPlayerList().getPlayers()) {
            if (PlayerDataManager.isJudged(player) && 
                PlayerDataManager.getLawyer(player) != null && 
                PlayerDataManager.getLawyer(player).equals(lawyer)) {
                return player;
            }
        }
        return null;
    }
}