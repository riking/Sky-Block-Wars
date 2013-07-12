package me.kyle.burnett.SkyBlockWarriors.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;

import net.minecraft.server.v1_6_R2.NBTBase;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;

import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {
	
	
	static InventoryUtil instance = new InventoryUtil();
	
	public static InventoryUtil getInstance(){
		return instance;
	}
	
	public Inventory getArmorInventory(PlayerInventory inventory) {
		ItemStack[] armor = inventory.getArmorContents();
		CraftInventoryCustom storage = new CraftInventoryCustom(null,
				armor.length);

		for (int i = 0; i < armor.length; i++)
			storage.setItem(i, armor[i]);

		return storage;
	}

	public Inventory getContentInventory(PlayerInventory inventory) {
		ItemStack[] content = inventory.getContents();
		CraftInventoryCustom storage = new CraftInventoryCustom(null,
				content.length);

		for (int i = 0; i < content.length; i++)
			storage.setItem(i, content[i]);

		return storage;
	}

	public String toBase64(Inventory inventory) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(outputStream);
		NBTTagList itemList = new NBTTagList();

		for (int i = 0; i < inventory.getSize(); i++) {
			NBTTagCompound outputObject = new NBTTagCompound();
			net.minecraft.server.v1_6_R2.ItemStack craft = getCraftVersion(inventory
					.getItem(i));

			if (craft != null)
				craft.save(outputObject);
			itemList.add(outputObject);
		}

		NBTBase.a(itemList, dataOutput);

		return new BigInteger(1, outputStream.toByteArray()).toString(32);

	}

	public Inventory fromBase64(String data) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				new BigInteger(data, 32).toByteArray());

		NBTTagList itemList = (NBTTagList) NBTBase.a(new DataInputStream(inputStream));
		Inventory inventory = new CraftInventoryCustom(null, itemList.size());

		for (int i = 0; i < itemList.size(); i++) {
			NBTTagCompound inputObject = (NBTTagCompound) itemList.get(i);

			if (!inputObject.isEmpty()) {
				inventory.setItem(i, CraftItemStack
						.asBukkitCopy(net.minecraft.server.v1_6_R2.ItemStack
								.createStack(inputObject)));
			}
		}

		return inventory;
	}

	private net.minecraft.server.v1_6_R2.ItemStack getCraftVersion(
			ItemStack stack) {
		if (stack != null)
			return CraftItemStack.asNMSCopy(stack);

		return null;
	}
}