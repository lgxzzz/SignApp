package com.qrcode.sign.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.smart.elevator.PersonOperateActivity;
import com.smart.elevator.R;
import com.smart.elevator.adapter.UserAdapter;
import com.smart.elevator.bean.User;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 * 管理员用户管理界面
 *
 * */
public class PersonMgrFragment extends Fragment {

    List<User> mUsers = new ArrayList<>();

    ListView mListView;

    UserAdapter mAdapter;

    Button mAddBtn;

    EditText mPersonSearchEd;

    Button mPersonSearchClearBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_person, container, false);
        initView(view);
        return view;
    }

    public static PersonMgrFragment getInstance() {
        return new PersonMgrFragment();
    }

    public void initView(View view){
        mPersonSearchEd = view.findViewById(R.id.person_search_ed);
        mPersonSearchClearBtn = view.findViewById(R.id.person_search_clear_btn);

        mListView = view.findViewById(R.id.person_list);

        mAdapter = new UserAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mAddBtn = view.findViewById(R.id.add_person_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), PersonOperateActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("opt","add");
                intent.putExtras(b);
                getContext().startActivity(intent);
            }
        });

        mPersonSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchData();
            }
        });

        mPersonSearchClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPersonSearchEd.setText("");
                searchData();
            }
        });
    };

    @Override
    public void onResume() {
        super.onResume();
        searchData();
    }

    //根据用户名搜索对应用户，没有填搜索词则返回全部用户
    public void searchData(){
        String value = mPersonSearchEd.getEditableText().toString();
        List<User> tempUsers = DBManger.getInstance(getContext()).QueryUsersByNameKey(value);
        if (tempUsers.size()>0){
            mUsers = tempUsers;
            mAdapter.setData(mUsers);
        }
    }
}
