package org.wangchenlong.animationplayer;

import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
            R.drawable.jessica_square, R.drawable.tiffany_square,
            R.drawable.taeyeon_square, R.drawable.yoona_square,
            R.drawable.yuri_square, R.drawable.soo_square,
            R.drawable.seo_square, R.drawable.kim_square,
            R.drawable.sunny_square
    };
    private @DrawableRes int mFrames = R.drawable.anim_images; // 帧动画图像
    private String[] mTexts = {
            "平移", "缩放", "旋转", "透明", "混合",
            "自定", "帧动", "Wrapper", "差值"
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);  // 绑定布局ID

        mContext = getApplicationContext();  // 上下文

        initAnimations(mContext);  // 创建动画组合

        mRvGrid.setLayoutManager(new GridLayoutManager(mContext, 2)); // 每行两列
        mRvGrid.setAdapter(new GridAdapter(mContext, mAnimations, mFrames, mTexts, mImages));  // 设置适配器
    }

    private void initAnimations(Context context) {
        mAnimations = new ArrayList<>();
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_translate));  // 平移动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_scale));  // 缩放动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_rotate));  // 旋转动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_alpha));  // 透明动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_all));  // 动画合集

        final Rotate3dAnimation anim = new Rotate3dAnimation(0.0f, 720.0f, 100.0f, 100.0f, 0.0f, false);
        anim.setDuration(2000);
        mAnimations.add(anim);  // 自定义动画
    }

    private static class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {

        private ArrayList<Animation> mAnimations;
        private @DrawableRes int mFrame;
        private String[] mTexts;
        private @DrawableRes int[] mImages;

        private int mLastPosition = -1;
        private Context mContext;

        public GridAdapter(Context context,
                           ArrayList<Animation> animations, @DrawableRes int frame,
                           String[] texts, @DrawableRes int[] images) {
            mContext = context;
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
            setAnimation(holder.getContainer(), position);  // 设置左侧滑动的动画效果

            holder.getImageView().setImageResource(mImages[position]);
            holder.getButton().setText(mTexts[position]);

            switch (position) {
                case 6: // 使用帧动画
                    holder.getImageView().setImageResource(mFrame);
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            ((AnimationDrawable) holder.getImageView().getDrawable()).start();
                        }
                    });
                    break;
                case 7: // 使用Wrapper属性动画
                    performWrapperAnimation(holder.getImageView(), 0, Utils.dp2px(mContext, 120));
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            performWrapperAnimation(holder.getImageView(), 0, Utils.dp2px(mContext, 120));
                        }
                    });
                    break;
                case 8: // 使用差值属性动画
                    performListenerAnimation(holder.getImageView(), 0, Utils.dp2px(mContext, 120));
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            performListenerAnimation(holder.getImageView(), 0, Utils.dp2px(mContext, 120));
                        }
                    });
                    break;
                default:
                    holder.getImageView().setAnimation(mAnimations.get(position));
                    holder.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            holder.getImageView().startAnimation(mAnimations.get(position));
                        }
                    });
                    break;
            }
        }

        /**
         * RecyclerList设置每一项的加载动画
         *
         * @param viewToAnimate 项目视图
         * @param position      位置
         */
        private void setAnimation(View viewToAnimate, int position) {
            if (position > mLastPosition || mLastPosition == -1) {
                Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
                animation.setDuration(3000);
                viewToAnimate.startAnimation(animation);
                mLastPosition = position;
            }
        }

        /**
         * 通过差值执行属性动画
         *
         * @param view  目标视图
         * @param start 起始宽度
         * @param end   终止宽度
         */
        private void performListenerAnimation(final View view, final int start, final int end) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                // 持有一个IntEvaluator对象，方便下面估值的时候使用
                private IntEvaluator mEvaluator = new IntEvaluator();

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    // 获得当前动画的进度值，整型，1-100之间
                    int currentValue = (Integer) animator.getAnimatedValue();

                    // 获得当前进度占整个动画过程的比例，浮点型，0-1之间
                    float fraction = animator.getAnimatedFraction();
                    // 直接调用整型估值器通过比例计算出宽度，然后再设给Button
                    view.getLayoutParams().width = mEvaluator.evaluate(fraction, start, end);
                    view.requestLayout();
                }
            });
            valueAnimator.setDuration(2000).start();
        }

        /**
         * 通过Wrapper实现属性动画
         *
         * @param view  目标视图
         * @param start 起始宽度
         * @param end   终止宽度
         */
        private void performWrapperAnimation(final View view, final int start, final int end) {
            ViewWrapper vw = new ViewWrapper(view);
            ObjectAnimator.ofInt(vw, "width", start, end).setDuration(2000).start(); // 启动动画
        }

        // 视图包装, 提供Width的get和set方法
        private static class ViewWrapper {
            private View mView;

            public ViewWrapper(View view) {
                mView = view;
            }

            @SuppressWarnings("unused")
            public int getWidth() {
                return mView.getLayoutParams().width;
            }

            @SuppressWarnings("unused")
            public void setWidth(int width) {
                mView.getLayoutParams().width = width;
                mView.requestLayout();
            }
        }

        @Override public int getItemCount() {
            return mTexts.length;
        }
    }

    // 列表适配器的ViewHolder
    private static class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private Button mButton;
        private View mContainer;

        public GridViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView;
            mButton = (Button) itemView.findViewById(R.id.item_b_start);
            mImageView = (ImageView) itemView.findViewById(R.id.item_iv_img);
        }

        public View getContainer() {
            return mContainer;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public Button getButton() {
            return mButton;
        }
    }
}
