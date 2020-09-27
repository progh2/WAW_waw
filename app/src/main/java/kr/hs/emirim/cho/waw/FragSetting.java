package kr.hs.emirim.cho.waw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FragSetting extends Fragment implements View.OnClickListener {

    private View view;
    private IdpResponse mIdpResponse;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_frag_setting,container,false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            getActivity().finish();
        }

        mIdpResponse = IdpResponse.fromResultIntent(getActivity().getIntent());

        populateProfile();
        populateIdpToken();

        Button signoutbtn = (Button)view.findViewById(R.id.sign_out);
        signoutbtn.setOnClickListener(this);

        Button deleteuser = (Button)view.findViewById(R.id.delete_account);
        deleteuser.setOnClickListener(this);

        return view;
    }

    private void populateProfile() {
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        ImageView profileimg=(ImageView)view.findViewById(R.id.user_profile_picture);
        final Bitmap[] bitmap = new Bitmap[1];
        Thread mThread=new Thread(){
            @Override
            public void run(){
                try{
                    URL url=new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is=conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start();

        try{
            mThread.join();
            profileimg.setImageBitmap(bitmap[0]);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        TextView emailtxt=(TextView)view.findViewById(R.id.user_email);
        emailtxt.setText(TextUtils.isEmpty(user.getEmail())?"이메일 없음":user.getEmail());

        TextView usernametxt=(TextView)view.findViewById(R.id.user_display_name);
        usernametxt.setText(TextUtils.isEmpty(user.getDisplayName())?"No display name":user.getDisplayName());

        StringBuilder providerList=new StringBuilder(100);
        providerList.append("로그인된 이메일 : ");
        if(user.getProviderData()==null || user.getProviderData().isEmpty()){
            providerList.append("없음");
        }else{
            for(UserInfo profile: user.getProviderData()){
                String providerId=profile.getProviderId();
                if(GoogleAuthProvider.PROVIDER_ID.equals(providerId)){
                    providerList.append("Google");
                }else{
                    providerList.append(providerId);
                }
            }
        }
        TextView userenabled=(TextView)view.findViewById(R.id.user_enable_providers);
        userenabled.setText(providerList);
    }

    private void populateIdpToken() {
        String token=null;
        if(mIdpResponse!=null){
            token=mIdpResponse.getIdpToken();
        }
        if(token==null){
            view.findViewById(R.id.idp_token_layout).setVisibility(View.GONE);
        }else{
            ((TextView)view.findViewById(R.id.idp_token)).setText(token);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out :
                signOut();
                break;
            case R.id.delete_account :
                deleteAccountClicked();
            default:
                break;
        }
    }

    private void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("정말 계정을 삭제하시겠습니까?")
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAccount();
                            }
                        })
                .setNegativeButton("아니요",null)
                .create();
        dialog.show();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(), "계정 삭제 성공!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
//                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
//                            fragmentManager.beginTransaction().remove(Frag_setting.this).commit();
//                            fragmentManager.popBackStack();
                        } else {
                            Toast.makeText(getContext(), "계정 삭제 실패!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            getActivity().finish();
//                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
//                            fragmentManager.beginTransaction().remove(Frag_setting.this).commit();
//                            fragmentManager.popBackStack();
                        } else {

                        }
                    }
                });
    }

}

//
//public class Frag_setting extends Fragment implements View.OnClickListener {
//
//    private IdpResponse mIdpResponse;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(currentUser == null) {
//            finish();
//            return;
//        }
//
//        mIdpResponse = IdpResponse.fromResultIntent(getIntent());
//
//        setContentView(R.layout.activity_frag_setting);
//        populateProfile();
//        populateIdpToken();
//
//        Button signoutbtn = (Button) findViewById(R.id.sign_out);
//        signoutbtn.setOnClickListener(this);
//
//        Button deleteuser = (Button)findViewById(R.id.delete_account);
//        deleteuser.setOnClickListener(this);
//
//
//    }
//
//    private void populateProfile() {
//        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//
//        ImageView profileimg=(ImageView)findViewById(R.id.user_profile_picture);
//        final Bitmap[] bitmap = new Bitmap[1];
//        Thread mThread=new Thread(){
//            @Override
//            public void run(){
//                try{
//                    URL url=new URL(user.getPhotoUrl().toString());
//                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//
//                    InputStream is=conn.getInputStream();
//                    bitmap[0] = BitmapFactory.decodeStream(is);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        mThread.start();
//
//        try{
//            mThread.join();
//            profileimg.setImageBitmap(bitmap[0]);
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }
//
//        TextView emailtxt=(TextView)findViewById(R.id.user_email);
//        emailtxt.setText(TextUtils.isEmpty(user.getEmail())?"이메일 없음":user.getEmail());
//
//        TextView usernametxt=(TextView)findViewById(R.id.user_display_name);
//        usernametxt.setText(TextUtils.isEmpty(user.getDisplayName())?"No display name":user.getDisplayName());
//
//        StringBuilder providerList=new StringBuilder(100);
//        providerList.append("로그인된 이메일 : ");
//        if(user.getProviderData()==null || user.getProviderData().isEmpty()){
//            providerList.append("없음");
//        }else{
//            for(UserInfo profile: user.getProviderData()){
//                String providerId=profile.getProviderId();
//                if(GoogleAuthProvider.PROVIDER_ID.equals(providerId)){
//                    providerList.append("Google");
//                }else{
//                    providerList.append(providerId);
//                }
//            }
//        }
//        TextView userenabled=(TextView)findViewById(R.id.user_enable_providers);
//        userenabled.setText(providerList);
//    }
//
//    private void populateIdpToken() {
//        String token=null;
//        if(mIdpResponse!=null){
//            token=mIdpResponse.getIdpToken();
//        }
//        if(token==null){
//            findViewById(R.id.idp_token_layout).setVisibility(View.GONE);
//        }else{
//            ((TextView)findViewById(R.id.idp_token)).setText(token);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_out :
//                signOut();
//                break;
//            case R.id.delete_account :
//                deleteAccountClicked();
//            default:
//                break;
//        }
//    }
//
//    private void deleteAccountClicked() {
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setMessage("정말 계정을 삭제하시겠습니까?")
//                .setPositiveButton("예",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                deleteAccount();
//                            }
//                        })
//                .setNegativeButton("아니요",null)
//                .create();
//        dialog.show();
//    }
//
//    private void deleteAccount() {
//        AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "계정 삭제 성공!", Toast.LENGTH_LONG).show();
//                            finish();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "계정 삭제 실패!", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
//
//    private void signOut() {
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            finish();
//                        } else {
//
//                        }
//                    }
//                });
//    }
//}