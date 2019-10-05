package com.example.arrivavoznired;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class AsyncPathScraper extends AsyncTask<Void,Void,Void> {

    private String mDisplayPathUrl;
    private WeakReference<List<String>> pathListWeakReference;
    private WeakReference<BusCardViewAdapter.BusViewHolder> viewHolderWeakReference;

    AsyncPathScraper(String displayPathUrl, List<String> pathList, BusCardViewAdapter.BusViewHolder viewHolder){
        this.mDisplayPathUrl = displayPathUrl;
        this.pathListWeakReference =  new WeakReference<>(pathList);
        this.viewHolderWeakReference = new WeakReference<>(viewHolder);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document pathDocument = Jsoup.connect(mDisplayPathUrl).get();
            Elements tableRows = pathDocument.getElementsByTag("tr");
            for (int i = 0; i < tableRows.size(); i=i+2) {
                Elements tableData = tableRows.get(i).getElementsByTag("td");
                String pathNodeString = tableData.get(0).text()+" "+tableData.get(2).text();
                pathListWeakReference.get().add(pathNodeString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
