package com.example.zz.superresolution.ui.upload;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import com.example.zz.superresolution.R;
import com.example.zz.superresolution.databinding.FragmentUploadBinding;

public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;
private FragmentUploadBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        uploadViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(UploadViewModel.class);

    binding = FragmentUploadBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

//        final TextView textView = binding.textUpload;
//        uploadViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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