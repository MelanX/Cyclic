package com.lothrazar.cyclicmagic.item.food;
import java.util.List;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAppleEmerald extends ItemFood implements IHasRecipe {
  private static final int CONVTIME = 1200;
  public ItemAppleEmerald() {
    super(2, false);
    this.setAlwaysEdible();
  }
  @Override
  public void addRecipe() {
    GameRegistry.addShapelessRecipe(new ItemStack(this),
        Items.EMERALD,
        Items.GOLDEN_APPLE);
  }
  @Override
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack par1ItemStack) {
    return EnumRarity.RARE;
  }
  private void startConverting(EntityZombieVillager v, int t) {
    //      v.conversionTime = t;
    ObfuscationReflectionHelper.setPrivateValue(EntityZombieVillager.class, v, t, "conversionTime", "field_82234_d");
    v.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, t, Math.min(v.world.getDifficulty().getDifficultyId() - 1, 0)));
    v.world.setEntityState(v, (byte) 16);
    try {
      //       v.getDataManager().set(CONVERTING, Boolean.valueOf(true));
      DataParameter<Boolean> CONVERTING = ObfuscationReflectionHelper.getPrivateValue(EntityZombieVillager.class, v, "CONVERTING", "field_184739_bx");
      v.getDataManager().set(CONVERTING, Boolean.valueOf(true));
    }
    catch (Exception e) {}
  }
  @Override
  public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    if (entity instanceof EntityZombieVillager) {
      EntityZombieVillager zombie = ((EntityZombieVillager) entity);
      //this is what we WANT to do, but the method is protected. we have to fake it by faking the interact event
//      zombie.startConverting(1200);
 
        startConverting(zombie, CONVTIME);
      return true;
    }
    else if (entity instanceof EntityVillager) {
      EntityVillager villager = ((EntityVillager) entity);
      int count = 0;
      for (MerchantRecipe merchantrecipe : villager.getRecipes(player)) {
        if (merchantrecipe.isRecipeDisabled()) {
          //vanilla code as of 1.9.4 odes this (2d6+2) 
          merchantrecipe.increaseMaxTradeUses(villager.getEntityWorld().rand.nextInt(6) + villager.getEntityWorld().rand.nextInt(6) + 2);
          count++;
        }
      }
      if (count > 0) {
        UtilChat.addChatMessage(player, UtilChat.lang("item.apple_emerald.merchant") + count);
        itemstack.setCount(itemstack.getCount() - 1);//.stackSize--;
        if (itemstack.getCount() == 0) {
          itemstack = null;
        }
      }
      return true;
    }
    return super.itemInteractionForEntity(itemstack, player, entity, hand);
  }
  //  @Override
  //  public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
  //    if (entity instanceof EntityZombie) {
  //      EntityZombie zombie = ((EntityZombie) entity);
  //      //this is what we WANT to do, but the method is protected. we have to fake it by faking the interact event
  //      // ((EntityZombie)entity).startConversion(1200);
  //      if (zombie.isVillager()) {
  //        UtilEntity.addOrMergePotionEffect(entity, new PotionEffect(MobEffects.WEAKNESS, 10, 0));
  //        if (zombie.processInteract(player, hand, new ItemStack(Items.GOLDEN_APPLE))) {
  //          itemstack.stackSize--;
  //          if (itemstack.stackSize == 0) {
  //            itemstack = null;
  //          }
  //        }
  //        //UtilInventory.decrStackSize(player, currentItem);
  //        return true;
  //      }
  //    }
  //    else if (entity instanceof EntityVillager) {
  //      EntityVillager villager = ((EntityVillager) entity);
  //      int count = 0;
  //      for (MerchantRecipe merchantrecipe : villager.getRecipes(player)) {
  //        if (merchantrecipe.isRecipeDisabled()) {
  //          //vanilla code as of 1.9.4 odes this (2d6+2) 
  //          merchantrecipe.increaseMaxTradeUses(villager.getEntityWorld().rand.nextInt(6) + villager.getEntityWorld().rand.nextInt(6) + 2);
  //          count++;
  //        }
  //      }
  //      if (count > 0) {
  //        UtilChat.addChatMessage(player, UtilChat.lang("item.apple_emerald.merchant") + count);
  //        itemstack.stackSize--;
  //        if (itemstack.stackSize == 0) {
  //          itemstack = null;
  //        }
  //        //				 else{
  //        //					 UtilSound.playSound(player, villager.getPosition(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE);
  //        //				 }
  //      }
  //      return true;
  //    }
  //    return super.itemInteractionForEntity(itemstack, player, entity, hand);
  //  }
  public void addInformation(ItemStack held, EntityPlayer player, List<String> list, boolean par4) {
    list.add(UtilChat.lang("item.apple_emerald.text"));
  }
  @Override
  protected void onFoodEaten(ItemStack par1ItemStack, World world, EntityPlayer player) {
    UtilEntity.addOrMergePotionEffect(player, new PotionEffect(
        MobEffects.SATURATION,
        20 * Const.TICKS_PER_SEC,
        Const.Potions.I));
  }
}
