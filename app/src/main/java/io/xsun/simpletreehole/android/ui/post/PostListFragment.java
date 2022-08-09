package io.xsun.simpletreehole.android.ui.post;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.Post;
import io.xsun.simpletreehole.android.service.PostService;
import io.xsun.simpletreehole.android.service.Services;
import io.xsun.simpletreehole.android.ui.EndlessRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListFragment extends Fragment {

    public static final int PAGE_SIZE = 10;
    public static final String LOG_TAG = PostListFragment.class.getSimpleName();

    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostListAdapter listAdapter;
    private EndlessRecyclerViewScrollListener listScrollListener;

    private Context context;
    private ProgressBar loadingBar;
    private SwipeRefreshLayout listContainer;
    private PostService service = Services.getPostService();

    public PostListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_post_list, container, false);
        context = view.getContext();

        listContainer = view.findViewById(R.id.post_list_container);
        listContainer.setOnRefreshListener(this::reloadPost);

        recyclerView = view.findViewById(R.id.post_list);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);

        listScrollListener = new EndlessRecyclerViewScrollListener(llm, (page, totalItemsCount, view1) -> loadNextPage());
        recyclerView.addOnScrollListener(listScrollListener);

        listAdapter = new PostListAdapter(context, postList);
        recyclerView.setAdapter(listAdapter);

        loadingBar = view.findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.VISIBLE);
        reloadPost();

        return view;
    }


    private void loadNextPage() {
        loadingBar.setVisibility(View.VISIBLE);
        service.postList(postList.size(), PAGE_SIZE).enqueue(new PostFetchCallback(false));
    }

    private void reloadPost() {
        listScrollListener.resetState();
        service.postList(0, PAGE_SIZE).enqueue(new PostFetchCallback(true));
    }

    public class PostFetchCallback implements Callback<List<Post>> {
        private final boolean reload;

        public PostFetchCallback(boolean reload) {
            this.reload = reload;
        }

        private void finishLoading() {
            loadingBar.setVisibility(View.GONE);
            if (reload) {
                listContainer.setRefreshing(false);
            }
        }

        @Override
        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
            finishLoading();
            if (response.isSuccessful()) {
                var newList = response.body();
                if (newList == null) {
                    Log.w(LOG_TAG, "Response body is null");
                } else {
                    if (reload) {
                        postList.clear();
                    }
                    postList.addAll(newList);
                    listAdapter.notifyDataSetChanged();
                }
            } else {
                var errToast = Toast.makeText(context, "Fetch post failed: " + response.code(), Toast.LENGTH_SHORT);
                errToast.show();
                Log.w(LOG_TAG, "Post fetch failed: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<List<Post>> call, Throwable t) {
            finishLoading();
            Log.e(LOG_TAG, "Post fetch failed due to exception", t);
        }
    }

}