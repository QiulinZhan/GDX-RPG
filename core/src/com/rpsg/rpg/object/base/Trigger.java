package com.rpsg.rpg.object.base;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.JsonValue;
import com.rpsg.gdxQuery.$;
import com.rpsg.rpg.core.RPG;
import com.rpsg.rpg.object.base.items.BaseItem;

public class Trigger {
	List<TriggerItem> item = new ArrayList<>();
	int gold;
	String script;
	public boolean forceStop = false;
	
	public static class TriggerItem{
		public int id,count;
		public TriggerItem(int id, int count) {
			this.id = id;
			this.count = count;
		}
		public boolean hasItem(){
			for(BaseItem item : RPG.global.items)
				if(item.id == id && item.count >= count)
					return true;
			return false;
		}
	}
	
	public static Trigger fromJSON(JsonValue value){
		Trigger t = new Trigger();
		List<TriggerItem> item = new ArrayList<>();
		if(value.has("item"))for(JsonValue child : value.get("item"))
			item.add(new TriggerItem(child.getInt("id"), child.getInt("count")));
			
		t.item = item;
		t.gold = value.has("gold") ? value.getInt("gold") : 0;
		t.script = value.has("script") ? value.getString("script") : null;
		return t;
	}

	public boolean test() {
		return forceStop || ($.test(item, TriggerItem::hasItem) && TriggerContext.test(script));
	}
	
	public void forceStop(){
		this.forceStop = true;
	}
}
