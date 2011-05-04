package de.swagner.homeinvasion;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.os.Bundle;

public class InstructionsActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);
        
	    // Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.ad);
	    adView.loadAd(new AdRequest());
	    
    }

}
