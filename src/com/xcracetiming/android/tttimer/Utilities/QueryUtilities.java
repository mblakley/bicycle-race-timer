package com.xcracetiming.android.tttimer.Utilities;

public class QueryUtilities {
	public static class SelectBuilder{
		private String whereTable = "";
		private String fullQuery = "";
		
		public SelectBuilder(String table){
			whereTable = table;
			fullQuery = whereTable;
		}
		
		public static SelectBuilder Where(String whereTable){			
			return new SelectBuilder(whereTable);
		}
		
		public SelectBuilder Equals(Object equalTo){
			fullQuery += "=" + equalTo.toString();
			
			return this;
		}
		
		public SelectBuilder EqualsString(String equalTo){
			fullQuery += "='" + equalTo + "'";
			
			return this;
		}
		
		public SelectBuilder EqualsParameter(){
			fullQuery += "=?";
			
			return this;
		}
		
		public SelectBuilder NotEqualsParameter(){
			fullQuery += "!=?";
			
			return this;
		}
		
		public SelectBuilder And(String nextTable){
			fullQuery += " and " + nextTable;
			
			return this;
		}
		
		public SelectBuilder Or(String nextTable){
			fullQuery += " or " + nextTable;
			
			return this;
		}
		
		public SelectBuilder GTE(Object greaterThanOrEqualTo){
			fullQuery += " >= " + greaterThanOrEqualTo;
			
			return this;
		}
		
		public SelectBuilder LTE(Object lessThanOrEqualTo){
			fullQuery += " <= " + lessThanOrEqualTo;
			
			return this;
		}
		
		public SelectBuilder GT(Object greaterThan){
			fullQuery += " > " + greaterThan;
			
			return this;
		}
		
		public SelectBuilder LT(Object lessThan){
			fullQuery += " < " + lessThan;
			
			return this;
		}
		
		@Override
		public String toString() {
			return fullQuery;
		}
	}

}
