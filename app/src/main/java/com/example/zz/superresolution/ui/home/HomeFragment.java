package com.example.zz.superresolution.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.example.zz.superresolution.HistoryActivity;
import com.example.zz.superresolution.LoginActivity;
import com.example.zz.superresolution.R;
import com.example.zz.superresolution.VIPActivity;
import com.example.zz.superresolution.WalletActivity;
import com.example.zz.superresolution.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    final String PREFS_NAME = "userinfo";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        NavigationView myselfView = (NavigationView) root.findViewById(R.id.myself_view);
        myselfView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.history:
                        Intent intent1 = new Intent(getActivity(), HistoryActivity.class);
                        startActivity(intent1);
                        //Toast.makeText(getContext(), "You clicked history button", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.vip:
                        Intent intent2 = new Intent(getContext(), VIPActivity.class);
                        startActivity(intent2);
//                        Toast.makeText(getContext(), "You clicked vip button", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.wallet:
                        Intent intent3 = new Intent(getContext(), WalletActivity.class);
                        startActivity(intent3);
//                        Toast.makeText(getContext(), "You clicked wallet button", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(getContext(), "You clicked settings button", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.service:
                        Toast.makeText(getContext(), "You clicked service button", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        Button logout_btn = (Button) root.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("logout","log out button pressed.");
                //TODO: logout button
                logout();
            }
        });

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void logout()  {
        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        Request request = new Request.Builder()
                .url("http://101.35.24.184:9008/user/logout")
                .header("Cookie", "token="+token)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("logout", "onFailure: ");
                e.printStackTrace();
                showResult("退出登录失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("logout", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("logout", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
//                    try{
//                        JSONObject jsonObject = new JSONObject(responseStr).getJSONObject("data");
//                        Log.d("wallet", jsonObject.toString());
//                        balance = jsonObject.getString("balance");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.d("wallet", "error");
//                    }
                    Log.d("logout","退出登录成功");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else{
                    Log.d("logout", "logout failed");
                    showResult("退出登录失败");
                }

            }
        });
//        Thread.sleep(500);
    }

    public void showResult(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken(){
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}