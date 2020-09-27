package kr.hs.emirim.cho.waw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

   private BottomNavigationView bottomNavigationView;
   private FragmentManager fm;
   private FragmentTransaction ft;
   private FragPostStore ps;
   private FragPostChallenge pc;
   private FragChat ca;
   private FragSetting fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavi);

        BottomNavigationHelper.disableShiftMode(bottomNavigationView);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_post_store:
                        MainActivity.this.setFrag(0);
                        break;
                    case R.id.action_post_chall:
                        MainActivity.this.setFrag(1);
                        break;
                    case R.id.action_chat_ask:
                        MainActivity.this.setFrag(2);
                        break;
                    case R.id.action_setting:
                        MainActivity.this.setFrag(3);
                        break;
                }
                return true;
            }
        });

        ps= new FragPostStore();
        pc= new FragPostChallenge();
        ca= new FragChat();
        fs= new FragSetting();
        setFrag(0); // 첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택.
    }

    //프래그먼트 교체가 일어나는 실행 함수
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, ps);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, pc);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, ca);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, fs);
                ft.commit();
                break;
        }
    }

}