package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose:EditText
    lateinit var btnTweet:Button
    lateinit var tvCounter:TextView
    lateinit var client: TwitterClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCounter = findViewById(R.id.tvCount)
        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                var count = s.toString().length.toString()
                if(count == "280"){
                    tvCounter.setTextColor(Color.RED)
                }else{
                    tvCounter.setTextColor(Color.BLACK)
                }
                tvCounter.setText("$count / 280")

            }
        })

        //handle the user's click on the tweet button
        btnTweet.setOnClickListener{

            //get the content of etCompose
            val tweetContent = etCompose.text.toString()

            //check if the tweet is empty
            if(tweetContent.isEmpty()){
                Toast.makeText(
                    this,
                    "Empty tweets not allowed!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }else{
                //check the character count
                if(tweetContent.length>280){
                    Toast.makeText(
                        this,
                        "Tweet is too long! Maximum characters allowed is 280",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }else{
                    //API call to publish the tweet
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){

                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            Log.i(TAG,"Successfully published tweet!")
                            // send the tweet back to TimeLineActivity
                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK,intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {

                            Log.i(TAG, "Failed to publish tweet", throwable)
                        }

                    })
                }
            }

        }
    }

    companion object{
        val TAG ="ComposeActivity"
    }
}