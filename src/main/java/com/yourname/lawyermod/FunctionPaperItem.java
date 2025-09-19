package com.yourname.lawyermod.items;

import com.yourname.lawyermod.PlayerDataManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class FunctionPaperItem extends Item {

    public FunctionPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            // Verifica se o jogador já tem função
            if (PlayerDataManager.getPlayerFunction(player) != null) {
                player.sendSystemMessage(Component.literal("§cVocê já possui uma função e não pode usar este papel!"));
                return InteractionResultHolder.fail(stack);
            }

            // Ativa modo de seleção de função
            PlayerDataManager.setSelectingFunction(player, true);
            player.sendSystemMessage(Component.literal("§aVocê agora pode escolher sua função!"));
            player.sendSystemMessage(Component.literal("§eUse /selectfunction <lawyer|angel> para escolher sua função."));

            // Remove o item do inventário após o uso
            stack.shrink(1);

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}
