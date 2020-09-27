package kr.hs.emirim.cho.waw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FragPostStore extends Fragment {

    private static final String TAG="MyTag";
    private View view;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private WritePostActivity wp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_frag_post_store,container,false);

        view.findViewById(R.id.btn_post_store).setOnClickListener(onClickListener);
        wp= new WritePostActivity();

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fm = getActivity().getSupportFragmentManager();
            ft = fm.beginTransaction();
            switch (v.getId()) {
                case R.id.btn_post_store:
                    ft.replace(R.id.main_frame, wp);
                    ft.commit();
                    break;
            }
        }
    };
}