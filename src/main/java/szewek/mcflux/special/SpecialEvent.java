package szewek.mcflux.special;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.error.ErrMsgThrownException;

public final class SpecialEvent {
	public final String description;
	final int colorBox, colorRibbon;
	private final SpecialItem[] items;
	public final long endTime;

	private SpecialEvent(String desc, int cbox, int cribbon, SpecialItem[] sis, long et) {
		description = desc;
		colorBox = cbox;
		colorRibbon = cribbon;
		items = sis;
		endTime = et;
	}

	static SpecialEvent fromJSON(JsonObject jo) {
		SpecialEvent ev = null;
		try {
			String d = jo.get("desc").getAsString();
			int cb = jo.get("box").getAsInt();
			int cr = jo.get("ribbon").getAsInt();
			long et = jo.get("ends").getAsLong();
			JsonArray ja = jo.getAsJsonArray("items");
			SpecialItem[] sis = new SpecialItem[ja.size()];
			for (int i = 0; i < ja.size(); i++) {
				JsonArray ji = ja.get(i).getAsJsonArray();
				short m = 0;
				String t = null;
				if (ji.size() > 2)
					m = ji.get(2).getAsShort();
				if (ji.size() > 3)
					t = ji.get(3).getAsString();
				sis[i] = new SpecialItem(ji.get(0).getAsString(), ji.get(1).getAsByte(), m, t);
			}
			ev = new SpecialEvent(d, cb, cr, sis, et);
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsgThrownException(e));
		}
		return ev;
	}

	public ItemStack[] createItems() {
		ItemStack[] iss = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			if (items[i].item != null) {
				iss[i] = new ItemStack(items[i].item, items[i].amount, items[i].meta);
				if (items[i].tag != null) {
					iss[i].setTagCompound(items[i].tag.copy());
				}
			}
		}
		return iss;
	}

	static class SpecialItem {
		final String name;
		final Item item;
		final byte amount;
		final short meta;
		final NBTTagCompound tag;

		SpecialItem(String n, byte c, short m, String t) {
			name = n;
			amount = c;
			meta = m;
			if (t != null) {
				NBTTagCompound nbt;
				try {
					nbt = JsonToNBT.getTagFromJson(t);
				} catch (NBTException e) {
					MCFluxReport.addErrMsg(new ErrMsgThrownException(e));
					nbt = null;
				}
				tag = nbt;
			} else
				tag = null;
			item = Item.getByNameOrId(name);
		}
	}
}
