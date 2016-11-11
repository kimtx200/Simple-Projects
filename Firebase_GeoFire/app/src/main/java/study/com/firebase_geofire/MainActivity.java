package study.com.firebase_geofire;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        GeoFire geoFire = new GeoFire(ref);

        String room_id1 = ref.push().getKey();
        geoFire.setLocation(room_id1, new GeoLocation(20.7853889, -112.4056973));

        String room_id2 = ref.push().getKey();
        geoFire.setLocation(room_id2, new GeoLocation(39.7853889, -120.4056973));

        String room_id3 = ref.push().getKey();
        //geoFire.setLocation(room_id3, new GeoLocation(11.7853889, -80.4056973));

        geoFire.setLocation(room_id3, new GeoLocation(37.7853889, -122.4056973), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    // 성공적으로 Insert 된 후에 처리

                    System.out.println("Location saved on server successfully!");
                }
            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("rooms/pushKey");
        GeoFire geoFire2 = new GeoFire(ref2);


        GeoLocation center = new GeoLocation(27.9875, -112.1654);
        float range = 10.0f;
        final Set<String> nearby = new HashSet<>();

        GeoQuery geoQuery = geoFire.queryAtLocation(center, range);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                nearby.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                nearby.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }
}
