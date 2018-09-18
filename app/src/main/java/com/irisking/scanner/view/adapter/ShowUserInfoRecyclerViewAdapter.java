package com.irisking.scanner.view.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.irisking.irisapp.R;
import com.irisking.scanner.bean.Dictionary;
import com.irisking.scanner.database.SqliteDataBase;
import com.irisking.scanner.view.custom.PlayingRelativeLayout;
import com.irisking.scanner.view.custom.dialog.CustomAlertDialog;
import com.irisking.scanner.view.custom.dialog.CustomEditTextDialog;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/17.
 */

public class ShowUserInfoRecyclerViewAdapter extends RecyclerView.Adapter<ShowUserInfoRecyclerViewAdapter.ViewHolder> {
	
	private Context context;
	private List<Map<String, String>> datas;
	private SqliteDataBase sqliteDataBase;
	private CustomAlertDialog customAlertDialog;
	private CustomEditTextDialog customEditTextDialog;
	private String oldName;
	private int identifyOrderSelected = -2;
	private int continueOrderSelected = -3;
	private int endOrderSelected = -4;
	private ObjectAnimator scaleAnim;
	private OnItemClickListener onItemClickListener;
	private boolean needReset;
	boolean isActionEnabled;
	
	public ShowUserInfoRecyclerViewAdapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.datas = datas;
		sqliteDataBase = SqliteDataBase.getInstance(context);
		identifyOrderSelected = -2;
		continueOrderSelected = -3;
		endOrderSelected = -4;
	}
	
	/**
	 * 传入需要伸缩动画的Order
	 *
	 * @param orderSelected
	 */
	public void setIdentifySelectOrder(int orderSelected) {
		this.identifyOrderSelected = orderSelected;
		notifyDataSetChanged();
	}

	public void setContinueSelectOrder(int orderSelected) {
		this.continueOrderSelected = orderSelected;
//		notifyDataSetChanged();
	}

	public void setContinueSelectOrderColor(int orderSelected) {
		this.endOrderSelected = orderSelected;
		notifyDataSetChanged();
	}
	
	public void setActionEnabled(boolean enabled) {
		this.isActionEnabled = enabled;
	}
	
	public void resetBackground() {
		needReset = true;
		notifyDataSetChanged();
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View view = LayoutInflater.from(context).inflate(R.layout.item_user_info_layout, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.item_tv_userName.setText(datas.get(position).get("name"));
		
		String order = datas.get(position).get("order");
		int oderIndex = 0;
		if (order != null) {
			try {
				oderIndex = Integer.parseInt(order);
			} catch (Exception e) {
				oderIndex = 0;
			}
		}
		if (oderIndex < 0 || oderIndex > Dictionary.ARR_USER_ICON.length - 1) {
			oderIndex = 0;
		}
		
		if (oderIndex == identifyOrderSelected) {
			holder.itemView.getOnFocusChangeListener();
			holder.mPlayRelativeLayout.play();
		}

//		if (needReset) {
//			holder.mPlayRelativeLayout.reset();
//		}

        if (oderIndex == continueOrderSelected) {
            scaleAnim = ObjectAnimator.ofFloat(holder.itemView, "ScaleX", 1.0f, 1.2f, 1.0f);
            scaleAnim.setDuration(60);
//			holder.itemView.setBackgroundColor(Color.parseColor("#dddddd"));
            scaleAnim.start();
//			holder.itemView.setBackgroundColor(Color.WHITE);
        }

		if (oderIndex == endOrderSelected) {
			holder.itemView.setBackgroundColor(Color.parseColor("#dddddd"));
		}

//        holder.itemView.setBackgroundColor(Color.WHITE);

//        if(oderIndex == orderSelected) {
//            //创建动画,这里的关键就是使用ArgbEvaluator, 后面2个参数就是 开始的颜色,和结束的颜色.
//            ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.GRAY);
//            colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int color = (int) animation.getAnimatedValue();//之后就可以得到动画的颜色了
//                    holder.itemView.setBackgroundColor(color);//设置一下, 就可以看到效果.
//                }
//            });
//            colorAnimator.setDuration(700);
//            colorAnimator.start();
//        }
		
		holder.item_iv_favicon.setImageResource(Dictionary.ARR_USER_ICON[oderIndex]);
		
		holder.item_ll_delete.setOnClickListener(new ActionListener(0, position));
		holder.item_ll_rename.setOnClickListener(new ActionListener(1, position));
		
		if (needReset) {
			holder.mPlayRelativeLayout.reset();
		}
	}
	
	@Override
	public int getItemCount() {
		return datas == null ? 0 : datas.size();
	}
	
	/**
	 * 设置Item点击监听
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public interface OnItemClickListener {
		void onItemClick(View view, int postion);
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		
		ImageView item_iv_favicon;
		TextView item_tv_userName;
		ImageView item_iv_delete;
		ImageView item_iv_rename;
		
		LinearLayout item_ll_delete;
		LinearLayout item_ll_rename;
		
		PlayingRelativeLayout mPlayRelativeLayout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			item_iv_favicon = (ImageView) itemView.findViewById(R.id.item_iv_user_favicon);
			item_tv_userName = (TextView) itemView.findViewById(R.id.item_tv_user_name);
			item_iv_delete = (ImageView) itemView.findViewById(R.id.item_iv_delete_user_info);
			item_iv_rename = (ImageView) itemView.findViewById(R.id.item_iv_rename_user_info);
			item_ll_delete = (LinearLayout) itemView.findViewById(R.id.ll_item_iv_delete_user_info);
			item_ll_rename = (LinearLayout) itemView.findViewById(R.id.ll_item_iv_rename_user_info);
			mPlayRelativeLayout = (PlayingRelativeLayout) itemView.findViewById(R.id.rootLayout);
		}
	}
	
	public class ActionListener implements View.OnClickListener {
		final int flag;
		int position;
		
		public ActionListener(int flag, int position) {
			this.flag = flag;
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if (!isActionEnabled) {
				view.setPressed(false);
				return;
			}
			if (flag == 0) {
				doDelete();
			}
			if (flag == 1) {
				doUpdate();
			}
		}
		
		void doDelete() {
			customAlertDialog = new CustomAlertDialog(context);
			customAlertDialog.setMessage("确定删除此用户吗？");
			customAlertDialog.setCancelOnclickListener("取消", new CustomAlertDialog.onCancelOnclickListener() {
				@Override
				public void onCancelClick() {
					customAlertDialog.dismiss();
				}
			});
			customAlertDialog.setDeleteOnclickListener("删除", new CustomAlertDialog.onDeleteOnclickListener() {
				@Override
				public void onDeleteClick() {
					sqliteDataBase.removeByFavicon(Integer.parseInt(datas.get(position).get("order")));
					Log.e("tony", "adapter ***position==" + position);
					datas.remove(position);
					Log.e("tony", "doDelete datas:" + datas);
					notifyDataSetChanged();
					customAlertDialog.dismiss();
				}
			});
			customAlertDialog.show();
		}
		
		void doUpdate() {
			oldName = datas.get(position).get("name");
			customEditTextDialog = new CustomEditTextDialog(context);
			customEditTextDialog.setMessage(oldName);
			customEditTextDialog.setCancelOnclickListener("取消", new CustomEditTextDialog.onCancelOnclickListener() {
				@Override
				public void onCancelClick() {
					customEditTextDialog.dismiss();
				}
			});
			customEditTextDialog.setConfirmOnclickListener("确定", new CustomEditTextDialog.onConfirmOnclickListener() {
				@Override
				public void onConfirmClick() {
					String newName = customEditTextDialog.getMessage();
					if (TextUtils.isEmpty(newName)) {
						return;
					} else {
						if (oldName.equals(newName)) {
							Toast.makeText(context, "两次用户名相同，请重新输入！", Toast.LENGTH_SHORT).show();
							customEditTextDialog.toggleInput(context);
						} else {
							sqliteDataBase.updateNameByFavicon(newName, Integer.parseInt(datas.get(position).get("order")));
							datas.get(position).put("name", newName);
							notifyDataSetChanged();
							customEditTextDialog.dismiss();
							customEditTextDialog.toggleInput(context);
						}
					}
					
				}
			});
			customEditTextDialog.show();
		}
	}
}
