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
import io.xsun.simpletreehole.android.data.Comment;
import io.xsun.simpletreehole.android.service.PostCommentService;
import io.xsun.simpletreehole.android.service.UserService;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private List<Comment> commentList;
    private final Fragment fragment;
    private final LayoutInflater inflater;

    public CommentListAdapter(Fragment fragment, Context context, List<Comment> commentList) {
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var itemView = inflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
            var post = commentList.get(position);
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

            var comment = commentList.get(position);
            var loggedUser = UserService.getInstance().getLoggedUser(fragment.requireContext());

            if (loggedUser != null) {
                likeButton.setOnClickListener(v -> {
                    PostCommentService.getInstance().toggleLikeComment(comment.getId(), loggedUser.getId(), res -> {
                        if (res.isOk()) {
                            commentList.set(position, res.getResult());
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
                bundle.putLong("postId", comment.getId());
                Utils.replaceFragment(fragment, PostDetailFragment.class, bundle);
            });
        }
    }
}
