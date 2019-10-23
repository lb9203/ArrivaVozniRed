package com.example.arrivavoznired;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class AsyncPathScraper extends AsyncTask<Void,Void,Void> {

    private WeakReference<Bus> busWeakReference;


    AsyncPathScraper(Bus bus){
        this.busWeakReference = new WeakReference<>(bus);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document pathDocument = Jsoup.connect(busWeakReference.get().displayPathUrl).get();
            Elements tableRows = pathDocument.getElementsByTag("tr");
            for (int i = 0; i < tableRows.size(); i=i+2) {
                Elements tableData = tableRows.get(i).getElementsByTag("td");
                String pathNodeString = tableData.get(0).text()+" "+tableData.get(2).text();
                busWeakReference.get().pathList.add(pathNodeString);
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
