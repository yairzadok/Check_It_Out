package yair.tzadok.importfilefromexcle_realtimefb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BottomDialog extends BottomSheetDialogFragment {

    private TextView title, link, btm_visit;
    private ImageView close;
    private String fetchURL;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_dialog, container, false);
        title = view.findViewById(R.id.txt_title);
        link = view.findViewById(R.id.txt_link);
        btm_visit = view.findViewById(R.id.visit);
        close = view.findViewById(R.id.close);

        title.setText(fetchURL);

        btm_visit.setOnClickListener(view1 -> {
                    Intent i = new Intent("android.intent.action.VIEW");
                    i.setData(Uri.parse(fetchURL));
                    startActivity(i);
        });

        close.setOnClickListener(view2 -> dismiss());
        return view;
    }

    public void FetchURL(String url) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fetchURL = url;
            }
        });
    }

}
