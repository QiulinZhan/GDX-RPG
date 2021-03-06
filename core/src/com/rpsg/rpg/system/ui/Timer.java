package com.rpsg.rpg.system.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.rpsg.gdxQuery.$;
import com.rpsg.gdxQuery.CustomRunnable;
import com.rpsg.rpg.core.Setting;
import com.rpsg.rpg.object.rpg.Enemy;
import com.rpsg.rpg.object.rpg.Hero;
import com.rpsg.rpg.object.rpg.Time;
import com.rpsg.rpg.system.base.Res;
import com.rpsg.rpg.utils.game.GameUtil;

public class Timer extends Group {
	
	private List<TimerClass> list = new ArrayList<>();
	private CustomRunnable<Time> callback;
	private static int total = 10000;
	private Runnable stop;
	private boolean isStop = false;
	
	public Timer(List<Hero> heroList, List<Enemy> enemyList,CustomRunnable<Time> callback,Runnable stop) {
		this.callback = callback;
		this.stop = stop;
		
		List<Time> objectList = new ArrayList<>();
		objectList.addAll(heroList);
		objectList.addAll(enemyList);
		
		//复制速度值
		$.each(objectList, obj -> list.add(new TimerClass(obj.getSimpleName(),obj, obj.getSpeed(),obj.getObjectColor())));
		
		$.add(Res.get(Setting.UI_BASE_IMG)).setSize(GameUtil.stage_width,28).setColor(.3f,.3f,.3f,.8f).appendTo(this);
		
		avg();
	}
	
	public void act(float delta) {
		if(isStop) return;
		
		int count = 0;
		for(TimerClass obj : list){
			if(obj.object instanceof Enemy && !obj.remove)
				count ++;
		}
		
		if(count == 0 && stop != null){
			stop.run();
			isStop = true;
			return;
		}
		
		for(TimerClass obj : list){
			if(!obj.globalPause && !obj.pause && !obj.remove && (obj.current += obj.speed) > total){
				//callback and reset
				callback.run(obj.object);
				obj.current = -MathUtils.random(500,1000);
			}
		}
		
		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		$.each(list, obj -> obj.actAndDraw(batch));
	}
	
	public void remove(Time object){
		$.getIf(list, obj -> obj.object == object,obj -> obj.remove = true);
	}
	
	public void setSpeed(Time object,int value){
		$.getIf(list, obj -> obj.object == object, obj -> obj.speed = value);
	}
	
	public void addSpeed(Time object,int value){
		$.getIf(list, obj -> obj.object == object, obj -> obj.speed += value);
	}
	
	public void pause(boolean flag){
		$.each(list, obj -> obj.globalPause = flag);
	}
	
	public void pause(Time object,boolean flag){
		$.getIf(list, obj -> obj.object == object, obj -> obj.pause = flag);
	}
	
	private void avg(){
		if(list.isEmpty()) return;
		Collections.sort(list);
		TimerClass max = list.get(0);
		float scale = (float)max.speed / 70f;
		$.each(list, obj -> obj.speed /= scale);
	}
	
	public class TimerClass extends Image implements Comparable<TimerClass> {
		public Time object;
		public int speed;
		public int current = 0;
		public boolean pause = false,globalPause = false;
		private Label label;
		public boolean remove = false;
		
		public TimerClass(String name,Time object, int speed, Color color) {
			this.object = object;
			this.speed = speed;
			setDrawable(Res.getDrawable(Setting.UI_BASE_IMG));
			setSize(48,28);
			setColor(color);
			this.current = MathUtils.random(0,total/2);
			label = Res.get(name, 20);
			label.size(48,28).align(Align.center);
		}

		public int compareTo(TimerClass o) {
			return o.speed - this.speed;
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			a(pause || remove ? .2f : .5f);
			label.a(pause || remove ? .3f : 1).x(getX()).draw(batch,parentAlpha);
		}
		
		@Override
		public void act(float delta) {
			setX(((float)current / (float)total) * GameUtil.stage_width);
			super.act(delta);
		}
	}
	
}
