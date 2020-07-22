package my.application.ui.moment;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import my.application.CreateChatroomActivity;
import my.application.R;
import my.application.SendMomentActivity;

public class MomentFragment extends Fragment {

    private MomentViewModel mViewModel;

    private Context mContext;

    public static MomentFragment newInstance() {
        return new MomentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moment_fragment, container, false);
        mContext = getActivity();
        setHasOptionsMenu(true);
        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.send_moment_actionbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 设置Send Moment按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_moment) {
            Intent intent = new Intent();
            intent.setClass(mContext, SendMomentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MomentViewModel.class);
        // TODO: Use the ViewModel
    }

}