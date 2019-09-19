package com.example.arrivavoznired;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class BusCache {

    private static String TAG = BusCache.class.getCanonicalName();

    public static String CACHE_FILENAME = "cache.bin";

    private static HashMap<String,ArrayList<Bus>> busCache = new HashMap<>();

    public static HashMap<String, ArrayList<Bus>> getBusCache() {
        return busCache;
    }

    /**
     * @param key String of key for cache, should be "[departureID]-[arrivalID]"
     * @return boolean true if cache contains the given route, false if otherwise.
     */
    public static boolean contains(String key){
        return busCache.containsKey(key);
    }


    /**
     * @param key String of key for cache, should be "[departureID]-[arrivalID]"
     * @return ArrayList of buses for the given route.
     */
    public static ArrayList<Bus> getBusListFromCache(String key){
        return busCache.get(key);
    }


    /**
     * @param key String of key for cache, should be "[departureID]-[arrivalID]" of route.
     * @param busList list of buses for the given route.
     */
    public static void putBusListIntoCache(String key,ArrayList<Bus> busList){
        busCache.put(key,busList);
    }


    /**
     * Prepares cache for use.
     * @param readerPackage ReaderPackage with filename to load cache from.
     */
    public static void loadCache(ReaderPackage readerPackage){
        new BusCache.Reader().execute(readerPackage);
    }


    /**
     * Saves cache into internal storage.
     * @param writerPackage WriterPackage with filename
     */
    public static void saveCache(WriterPackage writerPackage){
        new BusCache.Writer().execute(writerPackage);
    }


    /**
     * @param filename filename of cache file.
     * @return boolean true if cache file was deleted, false if not.
     */
    public static boolean invalidateCache(String filename){
        busCache = new HashMap<>();
        File cacheFile = new File(filename);
        return cacheFile.delete();
    }


    /**
     * This class writes all buses in a cache to a file.
     */
    public static class Writer extends AsyncTask<WriterPackage,Float,Void>{

        static String TAG = Writer.class.getCanonicalName();


        /**
         * @param writerPackages a WriterPackage that includes a filename and a HashMap of
         *                       input Strings and their respective bus arrays.
         */
        @Override
        protected Void doInBackground(WriterPackage... writerPackages) {
            String fileToWrite = writerPackages[0].filename;
            HashMap<String,ArrayList<Bus>> busMapToWrite = writerPackages[0].busCache;

            try {
                FileOutputStream fos = new FileOutputStream(fileToWrite);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(busMapToWrite);
                oos.close();
                fos.close();
                Log.d(TAG,"Bus cache successfully written to file.");
            } catch (FileNotFoundException e) {
                Log.d(TAG,e.getMessage());
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
            }

            return null;
        }
    }


    /**
     * This class reads all buses from the cache file.
     */
    public static class Reader extends AsyncTask<ReaderPackage,Float,HashMap<String,ArrayList<Bus> > >{

        static String TAG = Reader.class.getCanonicalName();


        /**
         * Loads cache into cache variable.
         * @param unserializedBusHashMap automatically provided.
         */
        @Override
        protected void onPostExecute(HashMap<String,ArrayList<Bus>> unserializedBusHashMap) {
            super.onPostExecute(busCache);
            busCache = unserializedBusHashMap;
        }


        /**
         * @param readerPackages a ReaderPackage that includes a filename.
         */
        @Override
        protected HashMap<String,ArrayList<Bus>> doInBackground(ReaderPackage... readerPackages) {
            HashMap<String,ArrayList<Bus>> retBusHashMap = new HashMap<String,ArrayList<Bus>>();
            String fileToRead = readerPackages[0].filename;

            try {
                FileInputStream fis = new FileInputStream(fileToRead);
                ObjectInputStream ois = new ObjectInputStream(fis);
                retBusHashMap = (HashMap<String,ArrayList<Bus>>) ois.readObject();
                ois.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG,e.getMessage());
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.d(TAG,e.getMessage());
            }

            return retBusHashMap;
        }
    }
}

/**
 * Data package for the BusCache.Reader class, needs filename to read cache from.
 */
class ReaderPackage{
    String filename;

    ReaderPackage(String filename) {
        this.filename = filename;
    }
}

/**
 * Data package for the BusCache.Writer class, needs filename to write cache to.
 */
class WriterPackage {
    String filename;
    HashMap<String,ArrayList<Bus>> busCache;

    WriterPackage(String filename, HashMap<String, ArrayList<Bus>> busCache){
        this.filename = filename;
        this.busCache = busCache;
    }
}
