package com.example.takahiro.localhazardmap_01.utility;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.takahiro.localhazardmap_01.entity.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by takahiro on 西暦15/09/15.
 */
public class DBAccesor extends SQLiteOpenHelper {

    private static DBAccesor instance;
    private static SQLiteDatabase db_entity;

    private static final String DB_FILE = "LocalHazardMap.db";
    private static final int DB_VERSION = 1;
    private static final String[][] TABLE_LIST = {{"organizations","_id integer primary key","name string not null","enable integer not null default 0"}};

    private DBAccesor(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    public static DBAccesor getInstance(Context context) {
       if(instance == null) {
           instance = new DBAccesor(context);
           db_entity = instance.getWritableDatabase();
       }
        return instance;
    }


    public ArrayList<ArrayList<String>> getRaws(Integer table_id, String[] columns, String where, String[] params, String order_by) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Cursor cursor = db_entity.query(TABLE_LIST[table_id][0],columns,where,params,null,null,order_by);
        while(cursor.moveToNext()) {
            ArrayList<String> raw = new ArrayList<String>();
            for(int i=0;i < cursor.getColumnCount();i++) {
                raw.add(cursor.getString(i));
            }
            result.add(raw);
        }
        return result;
    }

    public void insertRaw(int id, String[] params) {
        String query = "insert into "+TABLE_LIST[id][0]+" values(";
        for(int i=1;i < TABLE_LIST[id].length;i++) {
            String[] tmp = TABLE_LIST[id][i].split("[\\s]+");
            if(tmp[1].equals("string")) {
                query += "'"+params[i-1] + "',";
            } else {
                query += params[i-1] + ",";
            }
        }
        db_entity.execSQL(query.replaceAll(",$",");"));
    }

    public void updateRaw(String table_name, int id, String[] columns, String[] params) {
        String query = "update "+table_name+" set ";
        for(int i=0;i < columns.length;i++)  {
            query += columns[i]+" = "+params[i]+",";
        }
        query = query.replaceAll(",$"," where _id="+id+";");
        db_entity.execSQL(query);
    }

    public void updateOrganizations() {
        new GetOrgList().execute();
    }

   @Override
    public void onCreate(SQLiteDatabase db) {
       for(int i=0;i < TABLE_LIST.length;i++) {
           String sql = "create table " + TABLE_LIST[i][0] + "(";
           for(int j=1;j < TABLE_LIST[i].length;j++) {
               sql += TABLE_LIST[i][j] + ",";
           }
           db.execSQL(sql.replaceAll(",$",");"));
           updateOrganizations();
       }
   }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        for(int i=0;i < TABLE_LIST.length;i++) {
            db.execSQL("drop table " + TABLE_LIST[i][0] + ";");
        }
    }

    private class GetOrgList extends GetHttp {
        private GetOrgList() {
            super(Constants.SCHEME, Constants.AUTHORITY, "org/getList", new ArrayList<String>());
        }
        @Override
        protected void onPostExecute(String response) {
            ArrayList<String> existing_raws = new ArrayList<String>();
            for(ArrayList<String> raw : getRaws(0,null,null,null,null)){
                existing_raws.add(raw.get(0));
            }
            try {
                JSONObject organizations = new JSONObject(response).getJSONObject("response");
                Iterator<String> keys = organizations.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    boolean is_existed = false;
                    for(int i=0;i < existing_raws.size() && !is_existed;i++) {
                        is_existed = existing_raws.get(i).equals(key);
                    }
                    if(!is_existed) {
                        insertRaw(0, new String[]{key,organizations.getString(key),"0"});
                    }
                }
            } catch(JSONException error) {
            }
        }
    }

}
