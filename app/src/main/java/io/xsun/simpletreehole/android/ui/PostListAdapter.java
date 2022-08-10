package io.xsun.simpletreehole.android.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.Post;
import io.xsun.simpletreehole.android.service.PostCommentService;
import io.xsun.simpletreehole.android.service.UserService;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private List<Post> postList;
    private final Fragment fragment;
    private final LayoutInflater inflater;

    public PostListAdapter(Fragment fragment, Context context, List<Post> postList) {
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var itemView = inflater.inflate(R.layout.post_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView senderName, createTime, postContent, likeCount;
        private final ImageButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            createTime = itemView.findViewById(R.id.create_time);
            postContent = itemView.findViewById(R.id.post_content);
            likeCount = itemView.findViewById(R.id.like_count);
            likeButton = itemView.findViewById(R.id.like_button);
        }

        private void bindDisplayData(int position) {
            var post = postList.get(position);
            senderName.setText(post.getSender().getNickname());
            createTime.setText(post.getCreateTime().format(Utils.DATE_TIME_FORMATTER));
            postContent.setText(post.getContent());
            likeCount.setText(Integer.toString(post.getLikers().size()));

            var loggedUser = UserService.getInstance().getLoggedUser(fragment.requireContext());
            likeButton.setColorFilter(
                    post.getLikers().contains(loggedUser) ? Color.BLUE : Color.BLACK,
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        public void bindData(int position) {
            bindDisplayData(position);

            var post = postList.get(position);
            var loggedUser = UserService.getInstance().getLoggedUser(fragment.requireContext());

            if (loggedUser != null) {
                likeButton.setOnClickListener(v -> {
                    PostCommentService.getInstance().toggleLikePost(post.getId(), loggedUser.getId(), res -> {
                        if (res.isOk()) {
                            postList.set(position, res.getResult());
                            bindDisplayData(position);
                        } else {
                            Toast.makeText(
                                    fragment.requireContext(),
                                    "Toggle post like failed: " + res.getError().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

            postContent.setOnClickListener(v -> {
                var bundle = new Bundle();
                bundle.putLong("postId", post.getId());
                Utils.replaceFragment(fragment, PostDetailFragment.class, bundle);
            });
        }
    }
}
