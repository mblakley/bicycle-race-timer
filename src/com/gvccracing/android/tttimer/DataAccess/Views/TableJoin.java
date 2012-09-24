package com.gvccracing.android.tttimer.DataAccess.Views;

public class TableJoin {
	
	private String fullJoin;
	
	public TableJoin(String baseTable){
		fullJoin = baseTable;
	}
	
	public TableJoin LeftJoin(String joinFromTable, String joinToTable, String fromKey, String toKey){		
		fullJoin += " LEFT JOIN " + joinToTable + " on " + joinFromTable + "." + fromKey + "=" + joinToTable + "." + toKey;
		
		return this;
	}
	
	public TableJoin LeftOuterJoin(String joinFromTable, String joinToTable, String fromKey, String toKey){
		fullJoin += " LEFT OUTER JOIN " + joinToTable + " on " + joinFromTable + "." + fromKey + "=" + joinToTable + "." + toKey;
		
		return this;
	}
	
	public String toString(){
		return fullJoin;
	}
}