package compute.dtu.linc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.linc.R;
import com.nullwire.trace.ExceptionHandler;

import compute.dtu.linc.Util.WebServicesUtil;


public class WebViewActivity extends AppCompatActivity {
    private WebView wv;
    private String name;
    private String baseURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"https://tchoicedtu.herokuapp.com/bugReport");


        //Enable back button
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get data from intent
        name = getIntent().getStringExtra("name");
        baseURL = getIntent().getStringExtra("link");


        wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true); // enable javascript

        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                System.out.println("Intercepted a submit");
                return false;
            }
            public void onPageFinished(WebView view, String url) {

                view.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                Log.d("HTML", html);
                                Log.d("HTML", "Truth: " + html.contains("Your response has been recorded."));
                                if(html.contains("Your response has been recorded.") || html.contains("Dit svar er registreret")){
                                    WebServicesUtil.sendQuestionnaireCompletedRequest(name);
                                    finish();
                                }
                            }
                        });

            }
        });
        wv.loadUrl(baseURL);
    }

    //Override of default reutrn method
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Back button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
