package com.example.albertoarmijoruiz.fnance.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.ContainerAdaper;
import com.example.albertoarmijoruiz.fnance.helpers.DbHelper;
import com.example.albertoarmijoruiz.fnance.models.ContainerElement;

import java.util.ArrayList;

public class TodayFragment extends Fragment {

    private RecyclerView diaryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContainerAdaper adapter;
    private FloatingActionButton addButton;
    private DbHelper mHelper;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_today, container, false);

        mHelper = new DbHelper(getContext());

        // Obtenemos recyclerView y creamos el adapter.
        diaryRecycler = v.findViewById(R.id.today_recycler);
        diaryRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        diaryRecycler.setLayoutManager(mLayoutManager);
        ArrayList<ContainerElement> elements = mHelper.getTodayElements();
        adapter = new ContainerAdaper(elements, getContext());
        diaryRecycler.setAdapter(adapter);

        return v;
    }









}
