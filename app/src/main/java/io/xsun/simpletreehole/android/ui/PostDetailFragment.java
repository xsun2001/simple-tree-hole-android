package io.xsun.simpletreehole.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.Comment;
import io.xsun.simpletreehole.android.data.Post;
import io.xsun.simpletreehole.android.service.PostCommentService;
import io.xsun.simpletreehole.android.service.TaskRunner;
import io.xsun.simpletreehole.android.service.UserService;

public class PostDetailFragment extends Fragment {

    public static final int PAGE_SIZE = 10;

    private long postId;

    private TextView postSender, postTime, postLikeCount, postContent;

    private List<Comment> commentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentListAdapter listAdapter;
    private EndlessRecyclerViewScrollListener listScrollListener;

    private Context context;
    private ProgressBar loadingBar;
    private SwipeRefreshLayout listContainer;
    private EditText commentInput;
    private Button commentButton;

    private boolean loadPost, loadComment;

    public PostDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        context = view.getContext();

        postId = getArguments() == null ? -1 : getArguments().getLong("postId", -1);
        if (postId < 0) {
            Utils.replaceFragment(this, PostListFragment.class);
            return view;
        }

        var postHead = view.findViewById(R.id.post_detail_head);
        postSender = postHead.findViewById(R.id.sender_name);
        postTime = postHead.findViewById(R.id.create_time);
        postLikeCount = postHead.findViewById(R.id.like_count);
        postContent = postHead.findViewById(R.id.post_content);

        loadingBar = view.findViewById(R.id.post_detail_progress);
        commentInput = view.findViewById(R.id.post_detail_comment_input);
        commentButton = view.findViewById(R.id.post_detail_comment_send);
        commentButton.setOnClickListener(v -> sendComment());

        recyclerView = view.findViewById(R.id.post_detail_comment_list);
        var llm = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(llm);
        listScrollListener = new EndlessRecyclerViewScrollListener(llm, (page, totalItemsCount, view1) -> loadMoreComment());
        recyclerView.addOnScrollListener(listScrollListener);
        listAdapter = new CommentListAdapter(view.getContext(), commentList);
        recyclerView.setAdapter(listAdapter);

        listContainer = view.findViewById(R.id.post_list_container);
        listContainer.setOnRefreshListener(this::reloadData);

        reloadData();

        return view;
    }

    public class LoadPostCallback implements TaskRunner.Callback<Post> {
        @Override
        public void complete(TaskRunner.Result<Post> res) {
            if (res.isOk()) {
                var post = res.getResult();
                postSender.setText(post.getSender().getNickname());
                postTime.setText(post.getCreateTime().format(Utils.DATE_TIME_FORMATTER));
                postLikeCount.setText(Integer.toString(post.getLikers().size()));
                postContent.setText(post.getContent());
            } else {
                postContent.setText("Cannot load post. Please retry.");
                Toast.makeText(context, "Cannot load post: " + res.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadPost = false;
            if (!loadComment) {
                loadingBar.setVisibility(View.GONE);
                listContainer.setRefreshing(false);
            }
        }
    }

    public class LoadCommentCallback implements TaskRunner.Callback<List<Comment>> {
        private final boolean reload;

        public LoadCommentCallback(boolean reload) {
            this.reload = reload;
        }

        @Override
        public void complete(TaskRunner.Result<List<Comment>> res) {
            if (res.isOk()) {
                var comments = res.getResult();
                if (reload) {
                    commentList.clear();
                }
                commentList.addAll(comments);
                listAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Cannot load comments: " + res.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadComment = false;
            if (!loadPost) {
                loadingBar.setVisibility(View.GONE);
                listContainer.setRefreshing(false);
            }
        }
    }

    private void reloadData() {
        loadPost = true;
        loadComment = true;
        loadingBar.setVisibility(View.VISIBLE);
        PostCommentService.getInstance().getPost(postId, new LoadPostCallback());
        PostCommentService.getInstance().commentList(postId, 0, PAGE_SIZE, new LoadCommentCallback(true));
    }

    private void loadMoreComment() {
        loadComment = true;
        loadingBar.setVisibility(View.VISIBLE);
        PostCommentService.getInstance().commentList(postId, commentList.size(), PAGE_SIZE, new LoadCommentCallback(false));
    }

    private void sendComment() {
        commentButton.setClickable(false);
        var comment = commentInput.getText().toString();
        var user = UserService.getInstance().getLoggedUser(context);
        if (user == null) {
            Utils.replaceFragment(this, LoginFragment.class);
            Toast.makeText(context, "Please login", Toast.LENGTH_SHORT).show();
        } else {
            PostCommentService.getInstance().createComment(
                    postId,
                    user.getId(),
                    comment,
                    res -> {
                        if (res.isOk()) {
                            reloadData();
                            commentInput.setText("");
                            Toast.makeText(context, "Comment success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to comment: " + res.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        commentButton.setClickable(true);
                    });
        }
    }

}
