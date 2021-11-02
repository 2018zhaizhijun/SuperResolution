package com.example.zz.superresolution.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.example.zz.superresolution.HistoryActivity;
import com.example.zz.superresolution.R;
import com.example.zz.superresolution.VIPActivity;
import com.example.zz.superresolution.WalletActivity;
import com.example.zz.superresolution.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
private FragmentHomeBinding binding;

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
                        Intent intent1 = new Intent(getContext(), HistoryActivity.class);
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
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}