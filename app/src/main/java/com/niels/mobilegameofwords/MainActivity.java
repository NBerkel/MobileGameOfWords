package com.niels.mobilegameofwords;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static JSONArray sliderWords = new JSONArray();
    public static String nickname;

    static String ip = "http://gow.ddns.net/";
    String currentLocation;
    TextView currentLocationTextView;
    TextView welcomeTextView;
    Button playGameBtn;
    EditText usernameEditText;
    Boolean userAlreadyExists = false;
    String fileName = "nicknamem";

    DBGetLeaderboard dbGetLeaderboard = new DBGetLeaderboard(this);

    public static String getIP() { return ip; }

    public static String getNickname() {
        return nickname;
    }

    public static JSONArray getsliderWords() {
        return sliderWords;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        playGameBtn = (Button) findViewById(R.id.playGameBtn);

        dbGetLeaderboard.getLeaderboard(getApplicationContext());

        checkUsername();
        getCriteria();

        playGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAlreadyExists == false) {
                    if (checkUsernameInput() == true) {
                        // Write text file with username
                        String content = String.valueOf(usernameEditText.getText());

                        Log.d("Niels", "Create file");
                        FileOutputStream outputStream;
                        try {
                            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                            outputStream.write(content.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startGame();
                    }
                } else {
                    startGame();
                }
            }
        });
    }

    private void startGame() {
        LinearLayout intro_text = (LinearLayout) findViewById(R.id.intro_text);
        intro_text.setVisibility(View.GONE);

        //TODO: replace with inputLocRelevantWord()
        Fragment fragment = new inputLocRelevantWord();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void checkUsername() {
        //check if username already exist, else offer possibility to enter new username
        if (fileExistance(fileName) == true) {
            // remove username input
            LinearLayout linearLayoutUsernameInput = (LinearLayout) findViewById(R.id.linearLayoutUsernameInput);
            linearLayoutUsernameInput.setVisibility(View.GONE);

            Log.d("Niels", "File exist, lets read it!");
            userAlreadyExists = true;
            BufferedReader input;
            File file;
            try {
                file = new File(getFilesDir(), fileName);

                input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                Log.d("Niels", "File content " + buffer.toString());

                nickname = buffer.toString();
                welcomeTextView.setText("Welcome back " + nickname + "!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            userAlreadyExists = false;
        }
    }

    private boolean checkUsernameInput() {
        String userNameText = usernameEditText.getText().toString();
        if (userNameText.matches("")) {
            //Empty, no username provided.
            Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList nicknames = dbGetLeaderboard.nicknames;
        for (int i = 0; i < nicknames.size(); i++) {
            System.out.println(nicknames.get(i));


            String nickname = "";
            JSONObject jObj = (JSONObject) nicknames.get(i);
            try {
                nickname = jObj.getString("nicknames");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (userNameText.equals(nickname)) {
                Toast.makeText(this, "Username already in use", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
    }

    public boolean fileExistance(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private void getCriteria() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://dss.simohosio.com/api/getcriteria.php?question_id=8";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getJSONInfo(response);
                        playGameBtn.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                playGameBtn.setEnabled(false);
                Log.d("GameOfWords", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean getCurrentLocation() {
        boolean a = true;
        if (a) {
            // Beacon detection here.
            currentLocation = "UniversityA";
            currentLocationTextView.setText(currentLocation);
        } else {
            currentLocation = "";
            currentLocationTextView.setText("Current location not applicable.");
            a = false;
        }
        return a;
    }

    private void getJSONInfo(String currentLocation) {
        try {
            JSONArray strJson = new JSONArray(currentLocation);
            for (int i = 0; i < strJson.length(); i++) {
                JSONObject object = strJson.getJSONObject(i);

                JSONObject word = new JSONObject();
                word.put("criterion_id", object.getString("criterion_id"));
                word.put("criterion_body", object.getString("criterion_body"));
                sliderWords.put(word);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO block person from starting game?
        }
    }
}
