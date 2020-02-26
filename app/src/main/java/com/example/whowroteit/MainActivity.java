package com.example.whowroteit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String> {

    EditText mBookInput;
    TextView mTitleText, mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);

        if (getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,null);
        }
    }

    public void searchbooks(View view) {
        String queryString = mBookInput.getText().toString();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected() && queryString.length()!=0){

            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString",queryString);
//            getLoaderManager().restartLoader(0,queryBundle, null);
            getSupportLoaderManager().restartLoader(0,queryBundle,this);
            mTitleText.setText("Loading...");
            mAuthorText.setText("");

        }else if (queryString.length()==0){
            mAuthorText.setText("");
            mTitleText.setText("Please enter a search term");
        }else{
            mAuthorText.setText("");
            mTitleText.setText("Please check your connection and try again.");
        }
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookLoader(this,args.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i =0 ;i<itemsArray.length(); i++){
                JSONObject book = itemsArray.getJSONObject(i);
                String title = null;
                String authors = null;
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");

                }catch (Exception e){
                    e.printStackTrace();
                }
                if (title!= null && authors != null){
                    mTitleText.setText(title);
                    mAuthorText.setText(authors);
                    return;
                }
            }
            mTitleText.setText("No Results Found");
            mAuthorText.setText("");


        }catch (Exception e){

            mTitleText.setText("No Results Found");
            mAuthorText.setText("");
            e.printStackTrace();

        }
    }



    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
