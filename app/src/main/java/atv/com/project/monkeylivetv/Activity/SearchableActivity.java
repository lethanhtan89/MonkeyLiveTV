package atv.com.project.monkeylivetv.Activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import atv.com.project.monkeylivetv.R;

/**
 * Created by Administrator on 4/28/2016.
 */
public class SearchableActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        TextView textView = (TextView) findViewById(R.id.txtsearchable);
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            textView.setText("Searching by: " + query);
        }
        else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String uri = intent.getDataString();
            textView.setText("Suggestion: " + uri);
        }
    }
}
