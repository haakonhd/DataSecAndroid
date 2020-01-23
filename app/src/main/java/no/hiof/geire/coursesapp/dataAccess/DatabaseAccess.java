package no.hiof.geire.coursesapp.dataAccess;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Foreleser;
import no.hiof.geire.coursesapp.model.Melding;
import no.hiof.geire.coursesapp.model.Studieretning;

public class DatabaseAccess {



    public static ArrayList<Melding> getMeldingArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        ArrayList<Melding> messages = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Melding message = new Melding(obj.getInt("idMelding"), obj.getString("innhold"), obj.getInt("idForfatter"),
                    obj.getString("emnekode"), obj.getBoolean("rappotert"), obj.getInt("foreleser"), obj.getString("innholdSvar"));
            messages.add(message);
        }

        return messages;
    }

    public static ArrayList<Emne> getEmneArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        ArrayList<Emne> courses = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Emne course = new Emne(obj.getString("emnekode"), obj.getString("emnenavn"), obj.getInt("PIN"), obj.getInt("foreleser"));
            courses.add(course);
        }

        return courses;
    }

    public static ArrayList<Studieretning> getStudieretningArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        ArrayList<Studieretning> studyPrograms = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Studieretning studyProgram = new Studieretning(obj.getInt("idStudieretning"), obj.getString("studieretningNavn"));
            studyPrograms.add(studyProgram);
        }

        return studyPrograms;
    }

    public static ArrayList<Foreleser> getForeleserArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        ArrayList<Foreleser> lecturers = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Foreleser lecturer = new Foreleser(obj.getInt("idPerson"), obj.getString("epost"), obj.getString("navn"),
                    obj.getString("password"), obj.getBoolean("godkjentBruker"), obj.getString("bildeURL"));
            lecturers.add(lecturer);
        }

        return lecturers;
    }
}
