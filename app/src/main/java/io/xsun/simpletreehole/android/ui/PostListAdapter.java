package io.xsun.simpletreehole.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.Post;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private List<Post> postList;
    private final LayoutInflater inflater;

    public PostListAdapter(Context context, List<Post> postList) {
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
        private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            createTime = itemView.findViewById(R.id.create_time);
            postContent = itemView.findViewById(R.id.post_content);
            likeCount = itemView.findViewById(R.id.like_count);
        }

        public void bindData(int position) {
            var post = postList.get(position);
            senderName.setText(post.getSender().getNickname());
            createTime.setText(post.getCreateTime().format(formatter));
            postContent.setText(post.getContent());
            likeCount.setText(Integer.toString(post.getLikers().size()));
        }
    }
}
