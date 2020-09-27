package kr.hs.emirim.cho.waw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class WritePostActivity extends Fragment {
    private View view;
    private static final String TAG="WritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_write_post,container,false);


        view.findViewById(R.id.btn_check).setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_check:
                    storageUpload();
                    break;
            }
        }
    };

    private void storageUpload(){
        final String title=((EditText)view.findViewById(R.id.et_title)).getText().toString();
        final String contents=((EditText)view.findViewById(R.id.et_content)).getText().toString();

        if(title.length()>0 && contents.length()>0){
            user=FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeInfo=new WriteInfo(title, contents, user.getUid());
            uploader(writeInfo);
        }else{
            startToast("입력해주세요.");
        }
    }

    private void uploader(WriteInfo writeInfo){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentShanpshot wriiten ID : "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding document : "+e);
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();}

}
