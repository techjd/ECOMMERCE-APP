package com.hackathon.shoppy.SendGrid;

import android.os.AsyncTask;

import com.github.sendgrid.SendGrid;

import java.util.Hashtable;

public class SendGridAsyncTask extends AsyncTask<Hashtable<String,String>,Void,String> {

    @Override
    protected String doInBackground(Hashtable<String, String>... hashtables) {
        Hashtable<String,String> h = hashtables[0];
        SendGridCredentials credentials = new SendGridCredentials();
        SendGrid sendGrid = new SendGrid(credentials.getUsername(),credentials.getPassword());
        sendGrid.addTo(h.get("to"));
        sendGrid.setFromName("Shoppy");
        sendGrid.setFrom(h.get("from"));
        sendGrid.setSubject(h.get("subject"));
        sendGrid.setText(h.get("text"));
        String response = sendGrid.send();
        return response;
    }
}
