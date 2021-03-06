package com.rpsg.rpg.object.base;

import java.util.Date;

public class TaskInfo {
	public String name,description;
	public Integer icon;
	public Date date;
	public int id;
	public Class<? extends BaseTask> type;
	
	public static TaskInfo create(BaseTask task){
		TaskInfo info = new TaskInfo();
		info.date = new Date();
		info.name = task.name;
		info.description = task.description;
		info.icon = task.icon;
		info.id = task.id;
		info.type = task.getClass();
		return info;
	}
}
