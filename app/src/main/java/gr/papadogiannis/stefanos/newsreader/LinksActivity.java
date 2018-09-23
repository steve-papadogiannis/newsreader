package gr.papadogiannis.stefanos.newsreader;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

public class LinksActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);
        final List<Fragment> fragments = getFragments();
        final LinksPageAdapter linksPageAdapter = new LinksPageAdapter(getSupportFragmentManager(), fragments);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(linksPageAdapter);
        Toast.makeText(this, "Ολισθήστε αριστερά/δεξιά για να προβληθεί άλλος σύνδεσμος", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
    		finish();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }

    private List<Fragment> getFragments(){
    	final List<Fragment> fragmentList = new ArrayList<>();
    	for (int i = 0; i < 10; i++) {
    		fragmentList.add(LinkFragment.newInstance(i));
    	}
    	return fragmentList;
    }

}