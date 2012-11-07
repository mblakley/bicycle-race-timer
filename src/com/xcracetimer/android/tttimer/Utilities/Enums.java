package com.xcracetimer.android.tttimer.Utilities;

public class Enums {
	
	/**
	 * The listing of all possible racer categories and their descriptions
	 * @author Mark Blakley
	 *
	 */
	public enum RacerCategory{
		Cat123 (0, "Cat123"),
		Cat4 (1, "Cat4"),
		Cat5 (2, "Cat5"),
		Masters40Plus (3, "Masters40+"),
		Masters50Plus (4, "Masters50+"),
		Juniors (5, "Juniors"),
		Women (6, "Women");
		
		/**
		 * The ID of a racer category
		 */
		private final long racerCategoryID;
		/**
		 * The description of a racer category
		 */
		private final String racerCategoryDescription; 
		/**
		 * The constructor for a new race type - only used privately by the enum when a racer category is requested
		 * @param raceTypeID The ID of the racer category
		 * @param raceTypeDescription The description of the racer category
		 */
	    private RacerCategory(long racerCategoryID, String racerCategoryDescription) {
	        this.racerCategoryID = racerCategoryID;
	        this.racerCategoryDescription = racerCategoryDescription;
	    }
	    

	    /**
	     * Get the ID of this race type
	     * @return The ID of this race type
	     */
	    public long ID()   { return racerCategoryID; }
	    /**
	     * Get the description of this racer category
	     * @return The formatted description of this racer category
	     */
	    public String Description()   { return racerCategoryDescription; }
	    
	    public static String DescriptionFromRacerCategoryID(long racerCategoryID){
	    	String desc = ""; 
	    	switch((int)racerCategoryID){
	    		case 0:
	    			desc = RacerCategory.Cat123.Description();
	    			break;
	    		case 1:
	    			desc = RacerCategory.Cat4.Description();
	    			break;
	    		case 2:
	    			desc = RacerCategory.Cat5.Description();
	    			break;
	    		case 3:
	    			desc = RacerCategory.Masters40Plus.Description();
	    			break;
	    		case 4:
	    			desc = RacerCategory.Masters50Plus.Description();
	    			break;
	    		case 5:
	    			desc = RacerCategory.Juniors.Description();
	    			break;
	    		case 6:
	    			desc = RacerCategory.Women.Description();
	    			break;
	    	}
	    	
	    	return desc;
	    }
	    
	    public static Long RacerCategoryIDFromDescription(String desc){
	    	Long racerCategoryID = null; 
	    	if(desc == RacerCategory.Cat123.Description()){
	    		return RacerCategory.Cat123.ID();
	    	}
	    	else if(desc == RacerCategory.Cat4.Description()){
	    		return RacerCategory.Cat4.ID();
	    	}
	    	else if(desc == RacerCategory.Cat5.Description()){
	    		return RacerCategory.Cat5.ID();
	    	}
	    	else if(desc == RacerCategory.Masters40Plus.Description()){
	    		return RacerCategory.Masters40Plus.ID();
	    	}
	    	else if(desc == RacerCategory.Masters50Plus.Description()){
	    		return RacerCategory.Masters50Plus.ID();
	    	}
	    	else if(desc == RacerCategory.Juniors.Description()){
	    		return RacerCategory.Juniors.ID();
	    	}
	    	else if(desc == RacerCategory.Women.Description()){
	    		return RacerCategory.Women.ID();
	    	}
	    	return racerCategoryID;
	    }
	}

	/**
	 * The listing of all possible race types and their descriptions
	 * @author Mark Blakley
	 *
	 */
	public enum RaceType{
		TimeTrial (0, "Time Trial"),
		TeamTimeTrial (1, "Team Time Trial"),
		Criterium (2, "Criterium"),
		CircuitRace (3, "Circuit Race"),
		RoadRace (4, "Road Race");
		
		/**
		 * The ID of a race type
		 */
		private final long raceTypeID;
		/**
		 * The description of a race type
		 */
		private final String raceTypeDescription; 
		/**
		 * The constructor for a new race type - only used privately by the enum when a race type is requested
		 * @param raceTypeID The ID of the race type
		 * @param raceTypeDescription The description of the race type
		 */
	    private RaceType(int raceTypeID, String raceTypeDescription) {
	        this.raceTypeID = raceTypeID;
	        this.raceTypeDescription = raceTypeDescription;
	    }
	    /**
	     * Get the ID of this race type
	     * @return The ID of this race type
	     */
	    public long ID()   { return raceTypeID; }
	    /**
	     * Get the description of this race type
	     * @return The formatted description of this race type
	     */
	    public String Description()   { return raceTypeDescription; }
	    
	    public static String DescriptionFromRaceTypeID(long raceTypeID){
	    	String desc = ""; 
	    	switch((int)raceTypeID){
	    		case 0:
	    			desc = RaceType.TimeTrial.Description();
	    			break;
	    		case 1:
	    			desc = RaceType.TeamTimeTrial.Description();
	    			break;
	    		case 2:
	    			desc = RaceType.Criterium.Description();
	    			break;
	    		case 3:
	    			desc = RaceType.CircuitRace.Description();
	    			break;
	    		case 4:
	    			desc = RaceType.RoadRace.Description();
	    			break;
	    	}
	    	
	    	return desc;
	    }
	    
	    public static Long RaceTypeIDFromDescription(String desc){
	    	Long raceTypeID = null; 
	    	if(desc == RaceType.CircuitRace.Description()){
	    		return RaceType.CircuitRace.ID();
	    	}
	    	else if(desc == RaceType.Criterium.Description()){
	    		return RaceType.Criterium.ID();
	    	}
	    	else if(desc == RaceType.RoadRace.Description()){
	    		return RaceType.RoadRace.ID();
	    	}
	    	else if(desc == RaceType.TimeTrial.Description()){
	    		return RaceType.TimeTrial.ID();
	    	}
	    	else if(desc == RaceType.TeamTimeTrial.Description()){
	    		return RaceType.TeamTimeTrial.ID();
	    	}
	    	return raceTypeID;
	    }
	}
	
	/**
	 * The listing of all possible race types and their descriptions
	 * @author Mark Blakley
	 *
	 */
	public enum StartInterval{
		ThirtySeconds (30, "30 Seconds"),
		OneMinute (60, "1 Minute");
		
		/**
		 * The number of seconds in this start interval
		 */
		private final long startIntervalSeconds;
		/**
		 * The description of a start interval
		 */
		private final String startIntervalDescription; 
		
		/**
		 * The constructor for a new start interval - only used privately by the enum when a start interval is requested
		 * @param startIntervalSeconds The number of seconds in this start interval
		 * @param startIntervalDescription The description of the start interval
		 */
	    private StartInterval(int startIntervalSeconds, String startIntervalDescription) {
	        this.startIntervalSeconds = startIntervalSeconds;
	        this.startIntervalDescription = startIntervalDescription;
	    }
	    /**
	     * Get the ID of this race type
	     * @return The ID of this race type
	     */
	    public long Seconds()   { return startIntervalSeconds; }
	    /**
	     * Get the description of this race type
	     * @return The formatted description of this race type
	     */
	    public String Description()   { return startIntervalDescription; }
	    
	    /**
	     * Get the description to be displayed based on the number of seconds in the start interval
	     * @param startIntervalSeconds
	     * @return
	     */
	    public static String DescriptionFromStartIntervalSeconds(long startIntervalSeconds){
	    	String desc = ""; 
	    	switch((int)startIntervalSeconds){
	    		case 30:
	    			desc = StartInterval.ThirtySeconds.Description();
	    			break;
	    		case 60:
	    			desc = StartInterval.OneMinute.Description();
	    			break;
	    	}
	    	
	    	return desc;
	    }
	    
	    public static Long SecondsFromDescription(String desc){
	    	Long seconds = 60l; 
	    	if(desc == StartInterval.OneMinute.Description()){
	    		return StartInterval.OneMinute.Seconds();
	    	}
	    	else if(desc == StartInterval.ThirtySeconds.Description()){
	    		return StartInterval.ThirtySeconds.Seconds();
	    	}
	    	return seconds;
	    }
	}
}
