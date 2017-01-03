package org.wangchenlong.animationplayer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_rv_grid) RecyclerView mRvGrid;

    private Context mContext;
    private ArrayList<Animation> mAnimations;
    private @DrawableRes int[] mImages = {
            R.drawable.jessica_square,
            R.drawable.tiffany_square,
            R.drawable.taeyeon_square,
            R.drawable.yoona_square,
            R.drawable.yuri_square,
            R.drawable.seo_square
    };
    private @DrawableRes int mFrame = R.drawable.anim_images;
    private String[] mTexts = {
            "平移",
            "缩放",
            "旋转",
            "透明",
            "混合",
            "帧动"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        initAnimations(mContext);

        mRvGrid.setLayoutManager(new GridLayoutManager(mContext, 2)); // 每行两列
        mRvGrid.setAdapter(new GridAdapter(mAnimations, mFrame, mTexts, mImages));
    }

    private void initAnimations(Context context) {
        mAnimations = new ArrayList<>();
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_translate));
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_scale));
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_rotate));
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_alpha));
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_all));
    }

    private static class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {

        private ArrayList<Animation> mAnimations;
        private @DrawableRes int mFrame;
        private String[] mTexts;
        private @DrawableRes int[] mImages;

        public GridAdapter(ArrayList<Animation> animations, @DrawableRes int frame,
                           String[] texts, @DrawableRes int[] images) {
            mAnimations = animations;
            mFrame = frame;
            mTexts = texts;
            mImages = images;
        }

        @Override public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anim, parent, false);
            return new GridViewHolder(view);
        }

        @Override public void onBindViewHolder(final GridViewHolder holder, final int position) {
            holder.getImageView().setImageResource(mImages[position]);
            holder.getButton().setText(mTexts[position]);

            switch (position) {
                case 5:
                    holder.getImageView().setImageResource(mFrame);
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            ((AnimationDrawable) holder.getImageView().getDrawable()).start();
                        }
                    });
                    break;
                default:
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            holder.getImageView().startAnimation(mAnimations.get(position));
                        }
                    });
                    break;
            }
        }

        @Override public int getItemCount() {
            return mTexts.length;
        }
    }

    private static class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private Button mButton;

        public GridViewHolder(View itemView) {
            super(itemView);
            mButton = (Button) itemView.findViewById(R.id.item_b_start);
            mImageView = (ImageView) itemView.findViewById(R.id.item_iv_img);
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public Button getButton() {
            return mButton;
        }
    }
}
