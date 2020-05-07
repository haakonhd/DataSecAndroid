package no.hiof.geire.coursesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import no.hiof.geire.coursesapp.model.Person;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPerson;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SignInActivity extends AppCompatActivity {

    private Button SignInBtn, RegisterBtn;
    private TextView EmailTextView, PasswordTextView;
    private String jsonStringPersoner, jsonStringForelesere, jsonStringStudenter, jsonStringPersonInfo;
    private InputValidation iv = new InputValidation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInBtn = (Button) findViewById(R.id.signInBtn);
        RegisterBtn = (Button) findViewById(R.id.registerBtn);
        EmailTextView = (TextView) findViewById(R.id.emailTextView);
        PasswordTextView = (TextView) findViewById(R.id.passwordTexView);

        /*downloadForelesereJSON(getString(R.string.ip) + "/api/foreleser/read.php");
        downloadPersonerJSON(getString(R.string.ip) + "/api/person/read.php");
        downloadStudenterJSON(getString(R.string.ip) + "/api/student/read.php");*/

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    public void checkEmailAndPassword(){

        if(iv.isInputEmpty(EmailTextView.getText().toString()) || iv.isInputEmpty(PasswordTextView.getText().toString())) {
            Toast.makeText(this, "Email and password please", Toast.LENGTH_SHORT).show();
        }
        else {
            if(iv.emailIsValid(EmailTextView.getText().toString()) || iv.isInputEmpty(PasswordTextView.getText().toString()))
            try {
                checkPassword(EmailTextView.getText().toString(), PasswordTextView.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void checkPassword(String userEmail, String password) throws JSONException {

        //String passwordHash = makeHash(password);
        String json = "";
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.accumulate("epost", userEmail);
        jsonObject2.accumulate("passord", password);
        json = jsonObject2.toString();

        sendPostPasswordRight("http://158.39.188.222/api/person/passordErRiktig.php", json);
    }

    public void tokenShoit(String userEmail, String password) throws JSONException {

        getIdFromEmail("http://158.39.188.222/api/person/getIdFraEpost.php?epost=" + userEmail, password);
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateNewToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String string = base64Encoder.encodeToString(randomBytes);

        return string;
    }

    Person person;

    public void getInfoForLoggedInUser(String idPerson, String sessionToken) throws JSONException {
        String token = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("idPerson", idPerson);
        jsonObject.accumulate("sessionToken", sessionToken);
        token = jsonObject.toString();

        sendPostGetPerson("http://158.39.188.222/api/person/getPersonInfo.php", token);
    }


    /*public void checkEmailAndPassword2(){

        int logInValue = 0;
        Boolean foundUser = false;

        ArrayList<Foreleser> lecturers = new ArrayList<>();
        ArrayList<Person> persons = new ArrayList<>();
        ArrayList<Student> students = new ArrayList<>();

        try {
            lecturers = getForeleserArray(jsonStringForelesere);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            persons = getPersonArray(jsonStringPersoner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            students = getStudentArray(jsonStringStudenter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(EmailTextView.getText().toString().equals("") || PasswordTextView.getText().toString().equals("")) {
            Toast.makeText(this, "Email and password please", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i = 0; i < persons.size(); i++) {
                String email = persons.get(i).getEpost();
                String password = persons.get(i).getPassord();

                String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(EmailTextView.getText().toString());

                if (matcher.matches()){
                    if (EmailTextView.getText().toString().equals(email) && PasswordTextView.getText().toString().equals(password)) {

                        for (int x = 0; x < students.size(); x++) {

                            if (students.get(x).getIdStudent() == persons.get(i).getIdPerson()) {

                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("logged in as", 1);
                                intent.putExtra("id", persons.get(i).getIdPerson());
                                intent.putExtra("name", persons.get(i).getNavn());
                                startActivity(intent);
                                foundUser = true;
                                break;
                            }
                        }

                        for (int y = 0; y < lecturers.size(); y++) {

                            if (lecturers.get(y).getIdForeleser() == persons.get(i).getIdPerson()) {

                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("logged in as", 2);
                                intent.putExtra("id", persons.get(i).getIdPerson());
                                intent.putExtra("name", persons.get(i).getNavn());
                                startActivity(intent);
                                foundUser = true;
                                break;
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Email not valid format", Toast.LENGTH_SHORT).show();
                }

            }
        }
        if(foundUser == false) {
            Toast.makeText(this, "Email and/or password not valid", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void openRegiActivity(int registerValue){

        Intent intent = new Intent(this, RegiActivity.class);
        intent.putExtra("register as", registerValue);
        startActivity(intent);
    }

    public void showAlertDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Register");
        alert.setMessage("Register as?");
        alert.setNegativeButton("student", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openRegiActivity(0);
            }
        });
        alert.setPositiveButton("foreleser", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openRegiActivity(1);
            }
        });
        alert.create().show();
    }


    //---------------------------Database stuff-------------------------------------------------------------------------------------------------

    private void getPersonInfoJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringPersonInfo = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    String jsonStringUserId;

    private void getIdFromEmail(final String urlWebService, final String password) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String token = generateNewToken();

                jsonStringUserId = s;

                String newSessionTokenParams = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.accumulate("sessionToken", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.accumulate("passord", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.accumulate("idPerson", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newSessionTokenParams = jsonObject.toString();

                sendPostSetSessionToken("http://158.39.188.222/api/person/setSessionToken.php", newSessionTokenParams, token);
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }


    private void setJsonStringForelesere(String json){
        jsonStringForelesere = json;
    }

    private void downloadForelesereJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setJsonStringForelesere(s);
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void downloadPersonerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringPersoner = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void downloadStudenterJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringStudenter = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    String response = "";

    private void sendPost(final String urlString, final String json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", json);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    response = conn.getResponseMessage();

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void downloadPersonJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringStudenter = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void sendPostGetPerson(final String urlString, final String json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", json);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    if(String.valueOf(conn.getResponseCode()).equals("200")){
                        Person person = getPerson(jsonStringPersonInfo);
                    }

                    Toast.makeText(getApplicationContext(), person.toString(), Toast.LENGTH_SHORT).show();


                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private String makeHash(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        return hashed;
    }

    private void sendPostPasswordRight(final String urlString, final String json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", json);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    if(String.valueOf(conn.getResponseCode()).equals("200")) {

                        try {
                            tokenShoit(EmailTextView.getText().toString(), PasswordTextView.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void sendPostSetSessionToken(final String urlString, final String json, final String token) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", json);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    if(String.valueOf(conn.getResponseCode()).equals("200")) {

                        try {
                            getInfoForLoggedInUser(jsonStringUserId, token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
