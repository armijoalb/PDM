package com.example.albertoarmijoruiz.fnance;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DiaryFragment extends Fragment {

    private RecyclerView diaryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContainerAdaper adapter;
    private FloatingActionButton addButton;
    private DbHelper mHelper;

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance() {
        DiaryFragment fragment = new DiaryFragment();
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
        View v = inflater.inflate(R.layout.fragment_diary, container, false);

        mHelper = new DbHelper(getContext());

        // Obtenemos recyclerView y creamos el adapter.
        diaryRecycler = v.findViewById(R.id.recylerDiary);
        diaryRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        diaryRecycler.setLayoutManager(mLayoutManager);
        ArrayList<ContainerElement> elements = mHelper.getAllElements();
        adapter = new ContainerAdaper(elements);
        diaryRecycler.setAdapter(adapter);



        return v;
    }









}
