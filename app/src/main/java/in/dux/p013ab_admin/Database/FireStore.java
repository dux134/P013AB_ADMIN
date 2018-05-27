package in.dux.p013ab_admin.Database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/28/18.
 */

public class FireStore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void postWebsite( String filename, String fileUrl, String fileLocation) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", filename.trim());
        map.put("url", fileUrl.trim());
        map.put("location", fileLocation.trim());

        db.collection("websites").document(filename.toLowerCase().trim())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
