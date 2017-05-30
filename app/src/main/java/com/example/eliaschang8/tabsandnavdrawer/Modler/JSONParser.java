package com.example.eliaschang8.tabsandnavdrawer.Modler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eliaschang8.tabsandnavdrawer.Presenter.ArticleDes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by csaper6 on 5/15/17.
 */

public class JSONParser extends AsyncTask<String, Void, String> {
    private static final String TAG = "TAG";
    private ArrayList<String> urlList = new ArrayList<>();

    String postJSON = "";

    private ArrayList<PostItem>postsArray;
    Fragment fragmentActivity;
    ListView list;

    public JSONParser(Fragment fragmentActivity, ListView list){
        this.fragmentActivity = fragmentActivity;
        this.list = list;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            java.net.URL url = new java.net.URL(urls[0]);
            URLConnection connection = url.openConnection();

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null){
                postJSON += line;
                Log.d("onAsyncClass" , "Looping");
            }
            return postJSON;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("onAsyncClass" , "NOT WORKING dsfsdfsdfs");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        postsArray = new ArrayList<PostItem>();

        if(s != null){
            try {
                JSONArray postsJSON = new JSONArray(postJSON);

                for(int i = 0; i < postsJSON.length(); i++){

                    JSONObject objectPost =  postsJSON.optJSONObject(i);

                    //Get the Title
                    JSONObject renderedTitle = objectPost.optJSONObject("title");
                    String title = renderedTitle.optString("rendered");
                    title = android.text.Html.fromHtml(title).toString();
                    //Get the excerpt
                    JSONObject renderedExcerpt = objectPost.optJSONObject("excerpt");
                    String excerpt = renderedExcerpt.optString("rendered");
                    excerpt = android.text.Html.fromHtml(excerpt).toString();
                    //Get the author
                    JSONObject _embed = objectPost.optJSONObject("_embedded");
                    JSONArray array = _embed.optJSONArray("author");
                    JSONObject author = array.optJSONObject(0);
                    String name = author.optString("name");

                    String date  = objectPost.getString("date");
                    date = "Date: " + date.substring(date.indexOf("-")+1, date.indexOf("T")) + "-" + date.substring(0, date.indexOf("-"));

                    JSONObject betterFeaturedImage = objectPost.optJSONObject("better_featured_image");
                    JSONObject mediaDetail = betterFeaturedImage.optJSONObject("media_details");
                    JSONObject sizes = mediaDetail.optJSONObject("sizes");
                    JSONObject thumbnail = sizes.optJSONObject("thumbnail");
                    String ImgURL = thumbnail.optString("source_url");

                    JSONObject content = objectPost.optJSONObject("content");
                    String contentRendered = content.optString("rendered");
                    //contentRendered = android.text.Html.fromHtml(contentRendered).toString();

//                    int http ;
//                    int a = 0;
//                    while (a < )
//                    {
//                        http = contentRendered.indexOf("http", a);
//                        if(http == -1)
//                            break;
//                        String urlOne = contentRendered.substring(http, contentRendered.indexOf(" ", http) - 1);
//                        Log.d("HEEEEEEEEEEEYYYYYYYYYYY", urlOne);
//                        urlList.add(urlOne);
//                        a = http + urlOne.length();
//                    }

                    PostItem currentPost = new PostItem(title, excerpt, name, date, ImgURL, contentRendered);

                    postsArray.add(currentPost);

                    Log.d(TAG, "onPostExecute: " + ImgURL);
                }

                fillList();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fillList(){
        PostAdapter adapter = new PostAdapter(fragmentActivity.getActivity(), postsArray);

        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(fragmentActivity.getActivity(), "" + postsArray.get(position).getContent(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fragmentActivity.getActivity(), DesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", postsArray.get(position).getTitle());
                bundle.putString("DATE", postsArray.get(position).getDate());
                bundle.putString("AUTHOR", postsArray.get(position).getAuthor());
                bundle.putString("CONTENT", postsArray.get(position).getContent());
                intent.putExtras(bundle);

                fragmentActivity.startActivity(intent);
            }
        });
    }
}
