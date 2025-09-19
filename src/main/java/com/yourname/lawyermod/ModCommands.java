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

@Mod.EventBusSubscriber(modid = LawyerMod.MOD_ID)
public class ModCommands {

    // Registro dos comandos via evento
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Comando: /selectfunction <function>
        dispatcher.register(Commands.literal("selectfunction")
                .then(Commands.argument("function", StringArgumentType.string())
                        .executes(ModCommands::selectFunction)));

        // Comando: /julgar <player>
        dispatcher.register(Commands.literal("julgar")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::judgePlayer)));

        // Comando: /liberto
        dispatcher.register(Commands.literal("liberto")
                .executes(ModCommands::freePlayer));

        // Comando: /condenado
        dispatcher.register(Commands.literal("condenado")
                .executes(ModCommands::condemnPlayer));

        // Comando: /givefunctionpaper <player> (admin)
        dispatcher.register(Commands.literal("givefunctionpaper")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::giveFunctionPaper)));

        // Comando: /removefunction <player> (admin)
        dispatcher.register(Commands.literal("removefunction")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::removeFunction)));

        // Comando: /mission
        dispatcher.register(Commands.literal("mission")
                .executes(ModCommands::showMission));

        // Comandos de Anjo
        dispatcher.register(Commands.literal("heal")
                .executes(ModCommands::angelHeal));

        dispatcher.register(Commands.literal("fly")
                .executes(ModCommands::angelFly));

        dispatcher.register(Commands.literal("bless")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::angelBless)));
    }

    // ===== Funções principais dos comandos =====

    private static int selectFunction(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            String function = StringArgumentType.getString(context, "function");

            if (!PlayerDataManager.isSelectingFunction(player)) {
                player.sendSystemMessage(Component.literal("§cVocê precisa usar o papel de função primeiro!"));
                return 0;
            }

            switch (function.toLowerCase()) {
                case "lawyer" -> {
                    PlayerDataManager.setPlayerFunction(player, "lawyer");
                    PlayerDataManager.setSelectingFunction(player, false);
                    player.sendSystemMessage(Component.literal("§aVocê se tornou um Advogado!"));
                    player.sendSystemMessage(Component.literal("§eComandos: /julgar <player>, /liberto, /condenado"));
                    player.sendSystemMessage(Component.literal("§eUse /mission para ver sua missão!"));
                    return 1;
                }
                case "angel" -> {
                    PlayerDataManager.setPlayerFunction(player, "angel");
                    PlayerDataManager.setSelectingFunction(player, false);
                    player.sendSystemMessage(Component.literal("§bVocê se tornou um Anjo!"));
                    player.sendSystemMessage(Component.literal("§eBenefícios: Imortalidade, Voo, Cura"));
                    player.sendSystemMessage(Component.literal("§eComandos: /heal, /fly, /bless <player>"));
                    player.sendSystemMessage(Component.literal("§eUse /mission para ver sua missão!"));
                    return 1;
                }
                default -> player.sendSystemMessage(Component.literal("§cFunção não encontrada! Use: lawyer ou angel"));
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

                BlockPos pos = lawyer.blockPosition();
                target.teleportTo(pos.getX() + 2, pos.getY(), pos.getZ());

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

            ServerPlayer judged = findJudgedPlayer(lawyer);
            if (judged != null) {
                judged.setHealth(judged.getHealth() / 2);
                PlayerDataManager.freePlayer(judged);
                lawyer.sendSystemMessage(Component.literal("§aVocê libertou " + judged.getName().getString()));
                judged.sendSystemMessage(Component.literal("§aVocê foi libertado, mas perdeu metade da sua vida!"));
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

            ServerPlayer judged = findJudgedPlayer(lawyer);
            if (judged != null) {
                Level world = judged.getLevel();
                BlockPos randomPos = new BlockPos(
                        (int) (Math.random() * 2000 - 1000),
                        70,
                        (int) (Math.random() * 2000 - 1000)
                );

                judged.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
                PlayerDataManager.freePlayer(judged);
                lawyer.sendSystemMessage(Component.literal("§cVocê condenou " + judged.getName().getString()));
                judged.sendSystemMessage(Component.literal("§cVocê foi condenado e enviado para um local distante!"));
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
            String func = PlayerDataManager.getPlayerFunction(target);

            if (func != null) {
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

    // ===== Comandos de Anjo =====

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
                target.setHealth(target.getMaxHealth());
                target.getFoodData().setFoodLevel(20);

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

    // ===== Auxiliares =====
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
