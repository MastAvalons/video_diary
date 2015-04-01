package com.mti.videodiary.fragment;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.mti.videodiary.activity.CreateNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.adapter.NoteAdapter;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.dao.Video;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.data.manager.VideoDataManager;
import com.mti.videodiary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class NoteFragment extends BaseFragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private View mView;
    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ButtonFloat mButtonFloat;
    private ImageView mIvNote;
    private TextView mTvNoNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(Constants.UPDATE_ADAPTER_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note, container, false);

        initViews();
        setupRecycleView();
        initListeners();
        showEmptyView();

        return mView;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showEmptyView();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.UPDATE_NOTE_ADAPTER:
                NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

                final List<Note> notesList = noteDataManager.getAllNotesList();

                mAdapter.setListNotes(notesList);
                mAdapter.notifyDataSetChanged();

                showEmptyView();
                break;
        }
    }

    private void initViews() {
        mIvNote = (ImageView) mView.findViewById(R.id.ivCameraOff);
        mTvNoNotes = (TextView) mView.findViewById(R.id.tvNoRecords);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.noteRecycleView);
        mButtonFloat = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
    }

    private void initListeners() {
        mButtonFloat.setOnClickListener(this);
    }

    private void showEmptyView() {
        NoteDataManager noteManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        final List<Note> listNotes = noteManager.getAllNotesList();

        if (listNotes.isEmpty()) {
            mIvNote.setVisibility(View.VISIBLE);
            mTvNoNotes.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.ZoomIn);
            personalAnim.duration(DURATION);
            personalAnim.playOn(mIvNote);
            personalAnim.playOn(mTvNoNotes);

        } else {
            mIvNote.setVisibility(View.GONE);
            mTvNoNotes.setVisibility(View.GONE);
        }
    }


    private void setupRecycleView() {
        NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        final List<Note> listNotes = noteDataManager.getAllNotesList();

        mRecyclerView.setHasFixedSize(true);

        Display display = ((WindowManager) getActivity().getSystemService(MenuActivity.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == Surface.ROTATION_90)
            mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        else
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mAdapter = new NoteAdapter(getActivity(), listNotes);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager.setSpanCount(2);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFloat:
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                startActivityForResult(intent, Constants.UPDATE_NOTE_ADAPTER);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_note, menu);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

        final List<Note> listNotes = noteDataManager.getAllNotesList();

        ArrayList<Note> searchNoteList = new ArrayList<Note>();
        for (Note note : listNotes) {
            if (note.getTitle().contains(s))
                searchNoteList.add(note);
        }

        if (!searchNoteList.isEmpty())
            mAdapter.setListNotes(searchNoteList);

        mAdapter.notifyDataSetChanged();
        return false;
    }
}
