package com.lothrazar.cyclicmagic.gui.playerworkbench;
import com.lothrazar.cyclicmagic.gui.ContainerBase;
import com.lothrazar.cyclicmagic.util.UtilPlayerInventoryFilestorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPlayerExtWorkbench extends ContainerBase {
  public InventoryPlayerExtWorkbench craftMatrix;
  private final EntityPlayer thePlayer;
  private static final EntityEquipmentSlot[] ARMOR = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
  public static final int SLOT_SHIELD = 40;
  public static final int SQ = 18;
  public static final int VROW = 3;
  public static final int VCOL = 9;
  public static final int HOTBAR_SIZE = 9;
  public IInventory craftResult = new InventoryCraftResult();
  final int pad = 8;
  public ContainerPlayerExtWorkbench(InventoryPlayer playerInv, EntityPlayer player) {
    this.thePlayer = player;
    craftMatrix = new InventoryPlayerExtWorkbench(this, player);
    int slotId = 0;
    int xResult = 153, yResult = 42;
    this.addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, slotId, xResult, yResult));
    for (int k = 0; k < ARMOR.length; k++) {
      final EntityEquipmentSlot slot = ARMOR[k];
      slotId = 4 * VCOL + (VROW - k);
      this.addSlotToContainer(new Slot(playerInv, slotId, pad, pad + k * SQ) {
        @Override
        public int getSlotStackLimit() {
          return 1;
        }
        @Override
        public boolean isItemValid(ItemStack stack) {
          if (stack == null) {
            return false;
          }
          else {
            return stack.getItem().isValidArmor(stack, slot, thePlayer);
          }
        }
        @Override
        @SideOnly(Side.CLIENT)
        public String getSlotTexture() {
          return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
        }
      });
    }
    //the output
    int xPos, yPos;
    for (int i = 0; i < InventoryPlayerExtWorkbench.IROW; ++i) {
      for (int j = 0; j < InventoryPlayerExtWorkbench.ICOL; ++j) {
        xPos = pad + (j + 1) * SQ + 55;
        yPos = pad + i * SQ + 18;
        slotId = j + (i) * InventoryPlayerExtWorkbench.ICOL;
        this.addSlotToContainer(new Slot(craftMatrix, slotId, xPos, yPos));
      }
    }
    for (int i = 0; i < VROW; ++i) {
      for (int j = 0; j < VCOL; ++j) {
        xPos = pad + j * SQ;
        yPos = 84 + i * SQ;
        slotId = j + (i + 1) * HOTBAR_SIZE;
        this.addSlotToContainer(new Slot(playerInv, slotId, xPos, yPos));
      }
    }
    yPos = 142;
    for (int i = 0; i < HOTBAR_SIZE; ++i) {
      xPos = pad + i * SQ;
      slotId = i;
      this.addSlotToContainer(new Slot(playerInv, slotId, xPos, yPos));
    }
    this.onCraftMatrixChanged(craftMatrix);
  }
  @Override
  public void onCraftMatrixChanged(IInventory inventory) {
    craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, this.thePlayer.worldObj));
  }
  /**
   * Called when the container is closed.
   */
  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    if (!player.worldObj.isRemote) {
      //      UtilPlayerInventoryFilestorage.setPlayerInventory(player, inventory);
    }
  }
  /**
   * Called when a player shift-clicks on a slot. You must override this or you
   * will crash when someone does that.
   */
  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    ItemStack itemStack = null;
    Slot fromSlot = (Slot) this.inventorySlots.get(slotIndex);
    //ystem.out.println("  transferStackInSlot" + slotIndex);
    int craftOutpt = 0, playerStart = 15, playerEnd = 40 + 9, craftStart = 5, craftEnd = 13, armorStart = 1, armorEnd = 4;
    if (fromSlot != null && fromSlot.getHasStack()) {
      ItemStack itemStack1 = fromSlot.getStack();
      itemStack = itemStack1.copy();
      if (slotIndex == craftOutpt) {
        if (!this.mergeItemStack(itemStack1, playerStart, playerEnd + 1, false)) { return null; }
        fromSlot.onSlotChange(itemStack1, itemStack);
      }
      else if (slotIndex >= craftStart && slotIndex <= craftEnd) {
        if (!this.mergeItemStack(itemStack1, playerStart, playerEnd + 1, false)) {
          fromSlot.onSlotChanged();
          return null;
        }
      }
      else if (slotIndex >= armorStart && slotIndex <= armorEnd) {
        if (!this.mergeItemStack(itemStack1, playerStart, playerEnd + 1, false)) {
          fromSlot.onSlotChanged();
          return null;
        }
      }
      else if (slotIndex >= playerStart && slotIndex < playerEnd) {
        if (!this.mergeItemStack(itemStack1, craftStart, craftEnd + 1, false)) { return null; }
      }
      if (itemStack1.stackSize == 0) {
        fromSlot.putStack((ItemStack) null);
      }
      else {
        fromSlot.onSlotChanged();
      }
      if (itemStack.stackSize == itemStack1.stackSize) { return null; }
      fromSlot.onPickupFromSlot(this.thePlayer, itemStack1);
    }
    return itemStack;
  }
}
