package io.xsun.simpletreehole.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.Post;
import io.xsun.simpletreehole.android.service.PostCommentService;
import io.xsun.simpletreehole.android.service.TaskRunner;
import io.xsun.simpletreehole.android.service.UserService;

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

        view.findViewById(R.id.add_post).setOnClickListener(v -> getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragmentContainer,
                        UserService.getInstance().getLoggedUser(v.getContext()) == null ?
                                LoginFragment.class : CreatePostFragment.class,
                        null)
                .addToBackStack(null)
                .commit());

        return view;
    }

    private void loadNextPage() {
        loadingBar.setVisibility(View.VISIBLE);
        PostCommentService.getInstance().postList(postList.size(), PAGE_SIZE, new PostFetchCallback(false));
    }

    private void reloadPost() {
        listScrollListener.resetState();
        PostCommentService.getInstance().postList(0, PAGE_SIZE, new PostFetchCallback(true));
    }

    public class PostFetchCallback implements TaskRunner.Callback<List<Post>> {
        private final boolean reload;

        public PostFetchCallback(boolean reload) {
            this.reload = reload;
        }

        @Override
        public void complete(TaskRunner.Result<List<Post>> res) {
            loadingBar.setVisibility(View.GONE);
            if (reload) {
                listContainer.setRefreshing(false);
            }
            if (res.isOk()) {
                if (reload) {
                    postList.clear();
                }
                postList.addAll(res.getResult());
            } else {
                Log.e(LOG_TAG, "Post fetch failed due to exception", res.getError());
            }
        }
    }

}