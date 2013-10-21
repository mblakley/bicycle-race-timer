package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class TTProvider extends ContentProvider {

    // Database located at /data/data/com.gvccracing.android.tttimer/databases/GVCCRaces
    public static final String PROVIDER_NAME = "com.gvccracing.android.tttimer.DataAccess.TTProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "GVCCRaces";
    private static final int DATABASE_VERSION = 1;

    private Hashtable<String, ContentProviderTable> cptList = new Hashtable<String, ContentProviderTable>();

    private DatabaseHelper mDB;

    @Override
    public String getType(Uri arg0) {
        return "TTProvider";
    }

    @Override
    public boolean onCreate() {
        Log.v("TTProvider", "onCreate start");
        mDB = new DatabaseHelper(getContext());
        Log.v("TTProvider", "onCreate complete");
        return false;
    }

    /**
     * Creates and caches or retrieves a ContentProviderTable object from the lookup, based on the class name from the end of URI
     * @param uri
     * @return
     */
    private ContentProviderTable getClassInstance(Uri uri){
        ContentProviderTable cpt = null;
        String className = uri.getLastPathSegment();
        try {
            // Look in the cache of DB table objects
            if(cptList.containsKey(className)){
                // We found it!
                cpt = cptList.get(className);
            } else{
                // Didn't find it, so create a new one and put it in the cache
                cpt = (ContentProviderTable)Class.forName(className).newInstance();
                cptList.put(className, cpt);
            }
        } catch (ClassNotFoundException e) {
            Log.e("TTProvider.query", "Unable to create class of type " + className, e);
        } catch (IllegalAccessException e) {
            Log.e("TTProvider.query", "Unable to create class of type " + className, e);
        } catch (InstantiationException e) {
            Log.e("TTProvider.query", "Unable to create class of type " + className, e);
        }

        return cpt;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }


    @Override
    public Uri insert(Uri uri, ContentValues content) {

        Log.v("TTProvider", "insert start: uri=" + uri.toString());
        Uri resultUri = uri;
        ContentProviderTable cpt = getClassInstance(uri);

        ArrayList<Uri> notifyUris = cpt.getAllUrisToNotifyOnChange();

        long newRecordId = mDB.getWritableDatabase().insert(cpt.getTableName(), null, content);

        resultUri = Uri.withAppendedPath(resultUri, Long.toString(newRecordId));

        if(resultUri != uri){
            // notify all of the uris in the list
            for(Uri notify : notifyUris){
                getContext().getContentResolver().notifyChange(notify, null);
            }
        }
        Log.v("TTProvider", "insert complete: uri=" + uri.toString());

        return resultUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Log.v("TTProvider", "query start: uri=" + uri.toString() + " selection=" + selection);
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        ContentProviderTable cpt = getClassInstance(uri);

        qBuilder.setTables(cpt.getTableName());
        // Get the "group by" query parameter, or null if there is none
        String groupBy = uri.getQueryParameter(ContentProviderTable.GroupBy);
        // Get the "having" query parameter, or null if there is none
        String having = uri.getQueryParameter(ContentProviderTable.Having);
        // Get the "limit" query parameter, or null if there is none
        String limit = uri.getQueryParameter(ContentProviderTable.Limit);
        // Get the "distinct" query parameter, or null if there is none
        String distinctParam = uri.getQueryParameter(ContentProviderTable.Distinct);
        boolean distinct = true;

        // Convert distinct to a boolean, since that's what we're expecting.  Keep the default as true in case we get something unexpected (not true or false)
        if(distinctParam != null){
            distinct = distinctParam != "false";
        }

        // Get the ID parameter - This tells us if we should filter just by ID
        String getId = uri.getQueryParameter(BaseColumns._ID);
        if(getId != null){
            selection = BaseColumns._ID + "=" + getId;
            selectionArgs = null;
        }

        qBuilder.setDistinct(distinct);

        Cursor cpCursor = qBuilder.query(mDB.getReadableDatabase(),
                projection,
                selection,
                selectionArgs, groupBy, having, sortOrder,
                limit);

        cpCursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.v("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
        return cpCursor;
    }

    @Override
    public int update(Uri uri, ContentValues content, String selection, String[] selectionArgs) {
        Log.v("TTProvider", "update start: uri=" + uri.toString() + " selection=" + selection);

        int numChanged = 0;

        ContentProviderTable cpt = getClassInstance(uri);

        ArrayList<Uri> notifyUris = cpt.getAllUrisToNotifyOnChange();

        String getId = uri.getQueryParameter(BaseColumns._ID);
        if(getId == null){
            numChanged = mDB.getWritableDatabase().update(cpt.getTableName(), content, selection, selectionArgs);
        } else{
            numChanged = mDB.getWritableDatabase().update(cpt.getTableName(), content, BaseColumns._ID + "=" + getId, null);
        }

        // notify all of the uris in the list
        for(Uri notify : notifyUris){
            getContext().getContentResolver().notifyChange(notify, null);
        }

        Log.v("TTProvider", "update complete: uri=" + uri.toString() + " selection=" + selection);

        return numChanged;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v("TTProvider", "update start: uri=" + uri.toString() + " selection=" + selection);

        ContentProviderTable cpt = getClassInstance(uri);

        ArrayList<Uri> notifyUris = cpt.getAllUrisToNotifyOnChange();

        int numChanged = mDB.getWritableDatabase().delete(cpt.getTableName(), selection, selectionArgs);

        // notify all of the uris in the list
        for(Uri notify : notifyUris){
            getContext().getContentResolver().notifyChange(notify, null);
        }
        Log.v("TTProvider", "update complete: uri=" + uri.toString() + " selection=" + selection);
        return numChanged;
    }

	public static class DatabaseHelper extends SQLiteOpenHelper
	{
	    DatabaseHelper(Context context)
	    {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db)
	    {
	    	Log.w(TAG, "Creating " + DATABASE_NAME );

	    	db.execSQL(Racer.Instance().getCreate());
	        db.execSQL(RacerClubInfo.Instance().getCreate());
	        db.execSQL(Race.Instance().getCreate());
	        db.execSQL(RaceLocation.Instance().getCreate());
	        db.execSQL(RaceResults.Instance().getCreate());
	        db.execSQL(Primes.Instance().getCreate());
	        db.execSQL(UnassignedTimes.Instance().getCreate());
	        db.execSQL(RaceNotes.Instance().getCreate());
	        db.execSQL(TeamInfo.Instance().getCreate());
	        db.execSQL(TeamMembers.Instance().getCreate());
	        db.execSQL(TeamRaces.Instance().getCreate());
	        db.execSQL(AppSettings.Instance().getCreate());
	        db.execSQL(RaceLaps.Instance().getCreate());
	        db.execSQL(LookupGroups.Instance().getCreate());
	        // Initial load of lookup groups
	        db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Dry');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Moderate');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Humid');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Raining');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'A');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'B4');");
    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'B5');");
    		db.execSQL(LocationImages.Instance().getCreate());
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion,
	    int newVersion)
	    {
	        Log.w(TAG, "Upgrading database from version " + oldVersion
	                + " to "
	                + newVersion + ", all data will be preserved");
	    	if(oldVersion < 2 && newVersion >= 2){
	    		db.beginTransaction();
	    		try{
		    		db.execSQL("ALTER TABLE " + Race.Instance().getTableName() + " ADD COLUMN " + Race.NumLaps + " integer not null DEFAULT 1");
		    		db.execSQL("UPDATE " + Race.Instance().getTableName() + " SET " + Race.NumLaps + "=1");
		    		db.execSQL("ALTER TABLE " + RacerClubInfo.Instance().getTableName() + " ADD COLUMN " + RacerClubInfo.Upgraded + " integer not null DEFAULT 0");
		    		db.execSQL("UPDATE " + RacerClubInfo.Instance().getTableName() + " SET " + RacerClubInfo.Upgraded + "=0");    db.setTransactionSuccessful();
	    		} finally{
	    			db.endTransaction();
	    		}
	    	}
	    	if(oldVersion < 3 && newVersion >= 3){
	    		db.beginTransaction();
	    		try{
		    		db.execSQL("ALTER TABLE " + TeamMembers.Instance().getTableName() + " ADD COLUMN " + TeamMembers.TeamRacerNumber + " integer not null DEFAULT 0");
		    		db.execSQL("ALTER TABLE " + RaceResults.Instance().getTableName() + " ADD COLUMN " + RaceResults.TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") null");
		    		db.execSQL(RaceLaps.Instance().getCreate());
		    		db.execSQL("ALTER TABLE " + Racer.Instance().getTableName() + " ADD COLUMN " + Racer.USACNumber + " integer not null DEFAULT 0");
			        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
			        db.execSQL("ALTER TABLE " + TeamInfo.Instance().getTableName() + " ADD COLUMN " + TeamInfo.Year + " integer not null DEFAULT " + year);
			        // Need to change the RacerClubInfo_ID column to allow nulls.  Unfortunately, sqlite does not allow alter column statements,
			        // so we need to copy the data into a tmp table, create the table again using the new schema, then copy the data back in
			        // and drop the tmp table
			        db.execSQL("ALTER TABLE " + RaceResults.Instance().getTableName() + " RENAME TO tmp_" + RaceResults.Instance().getTableName() + ";");
			        db.execSQL(RaceResults.Instance().getCreate());
			        db.execSQL("INSERT INTO " + RaceResults.Instance().getTableName() + "(" + RaceResults._ID + "," + RaceResults.RacerClubInfo_ID + "," + RaceResults.TeamInfo_ID + ","
			        			+ RaceResults.Race_ID + "," + RaceResults.StartOrder + "," + RaceResults.StartTimeOffset + "," + RaceResults.StartTime + ","
			        			+ RaceResults.EndTime + "," + RaceResults.ElapsedTime + "," + RaceResults.OverallPlacing + "," + RaceResults.CategoryPlacing + ","
			        			+ RaceResults.Points + "," + RaceResults.PrimePoints
			        			+ ") SELECT " + RaceResults._ID + "," + RaceResults.RacerClubInfo_ID + "," + RaceResults.TeamInfo_ID + ","
			        			+ RaceResults.Race_ID + "," + RaceResults.StartOrder + "," + RaceResults.StartTimeOffset + "," + RaceResults.StartTime + ","
			        			+ RaceResults.EndTime + "," + RaceResults.ElapsedTime + "," + RaceResults.OverallPlacing + "," + RaceResults.CategoryPlacing + ","
			        			+ RaceResults.Points + "," + RaceResults.PrimePoints
			        			+ " FROM tmp_" + RaceResults.Instance().getTableName() + ";");
			        db.execSQL("DROP TABLE tmp_" +  RaceResults.Instance().getTableName() + ";");
			        db.execSQL("UPDATE " + RacerClubInfo.Instance().getTableName() + " SET " + RacerClubInfo.Category + "='W' WHERE " + RacerClubInfo.Category + "='Women'");
			        db.setTransactionSuccessful();
	    		} finally{
	    			db.endTransaction();
	    		}
	    	}
	    	if(oldVersion < 4 && newVersion >= 4){
	    		db.beginTransaction();
	    		try{
		    		db.execSQL("ALTER TABLE " + UnassignedTimes.Instance().getTableName() + " ADD COLUMN " + UnassignedTimes.RaceResult_ID + " integer null");
			        db.setTransactionSuccessful();
	    		} finally{
	    			db.endTransaction();
	    		}
	    	}
	    	if(oldVersion < 5 && newVersion >= 5){
	    		db.beginTransaction();
	    		try{
	    			db.execSQL(LookupGroups.Instance().getCreate());
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Dry');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Moderate');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Humid');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Raining');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'A');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'B4');");
		    		db.execSQL("INSERT INTO " + LookupGroups.Instance().getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'B5');");
		    		// Still need to do a bunch of updates to be able to use the category group, so we'll leave that until the next database upgrade
			        db.setTransactionSuccessful();
	    		} finally{
	    			db.endTransaction();
	    		}
	    	}
	    	if(oldVersion < 6 && newVersion >= 6){
	    		db.beginTransaction();
	    		try{
	    			db.execSQL(LocationImages.Instance().getCreate());
			        db.setTransactionSuccessful();
	    		} finally{
	    			db.endTransaction();
	    		}
	    	}
	    }
	}
}
