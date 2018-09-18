package com.irisking.scanner.view.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.BaseAdapter;

import com.irisking.irisapp.R;
import com.irisking.scanner.bean.Dictionary;
import com.irisking.scanner.database.SqliteDataBase;
import com.irisking.scanner.view.custom.dialog.CustomAlertDialog;
import com.irisking.scanner.view.custom.dialog.CustomEditTextDialog;

import java.util.List;
import java.util.Map;

public class ShowUserInfoAdapter extends BaseAdapter {
	private List<Map<String, String>> datas;
	private Context context;
	private LayoutInflater layoutInflater;
	private int orderSelected = -1;
	private SqliteDataBase sqliteDataBase;
	private CustomAlertDialog customAlertDialog;
	private CustomEditTextDialog customEditTextDialog;
	private String oldName;
	private ObjectAnimator scaleAnim;

	private DeleteCallBackItemListener deleteCallBackItemListener;
	private UpdateCallBackItemListener updateCallBackItemListener;

	public ShowUserInfoAdapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.datas = datas;
		layoutInflater = LayoutInflater.from(context);
		sqliteDataBase = SqliteDataBase.getInstance(context);
	}

	/**
	 * 传入需要伸缩动画的Order
	 *
	 * @param orderSelected
	 */
	public void setSelectOrder(int orderSelected) {
		this.orderSelected = orderSelected;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return (datas == null) ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_user_info_layout, null);

			viewHolder = new ViewHolder();
			viewHolder.item_iv_favicon =  convertView.findViewById(R.id.item_iv_user_favicon);
			viewHolder.item_tv_userName =  convertView.findViewById(R.id.item_tv_user_name);
			viewHolder.item_iv_delete =  convertView.findViewById(R.id.item_iv_delete_user_info);
			viewHolder.item_iv_rename =  convertView.findViewById(R.id.item_iv_rename_user_info);
			viewHolder.item_ll_delete =  convertView.findViewById(R.id.ll_item_iv_delete_user_info);
			viewHolder.item_ll_rename =  convertView.findViewById(R.id.ll_item_iv_rename_user_info);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.item_tv_userName.setText(datas.get(position).get("name"));

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

		if (oderIndex == orderSelected) {
			scaleAnim = ObjectAnimator.ofFloat(convertView, "ScaleX", 1.0f, 1.2f, 1.0f);
			scaleAnim.setDuration(750);
			scaleAnim.start();
		}

		viewHolder.item_iv_favicon.setImageResource(Dictionary.ARR_USER_ICON[oderIndex]);

		viewHolder.item_ll_delete.setOnClickListener(new ActionListener(0, position));
		viewHolder.item_ll_rename.setOnClickListener(new ActionListener(1, position));
		return convertView;
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
					sqliteDataBase.removeByName(datas.get(position).get("name"));
					Log.e("tony", "adapter ***position==" + position);
					datas.remove(position);
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
						sqliteDataBase.updateNameByFavicon(newName, Integer.parseInt(datas.get(position).get("order")));
						datas.get(position).put("name", newName);
						notifyDataSetChanged();
						customEditTextDialog.dismiss();
						customEditTextDialog.toggleInput(context);
					}

				}
			});
			customEditTextDialog.show();
		}
	}

	public class ViewHolder {
		ImageView item_iv_favicon;
		TextView item_tv_userName;
		ImageView item_iv_delete;
		ImageView item_iv_rename;

		LinearLayout item_ll_delete;
		LinearLayout item_ll_rename;
	}

	public DeleteCallBackItemListener getCallBackListener() {
		return deleteCallBackItemListener;
	}

	public void setCallBackListener(
			DeleteCallBackItemListener callBackItemListener) {
		this.deleteCallBackItemListener = callBackItemListener;
	}

	public UpdateCallBackItemListener getCallBackItemListener() {
		return updateCallBackItemListener;
	}

	public void setCallBackListener(
			UpdateCallBackItemListener callBackItemListener) {
		this.updateCallBackItemListener = callBackItemListener;
	}


	public interface DeleteCallBackItemListener {
		void deleteCallBackItem(int position);
	}

	public interface UpdateCallBackItemListener {
		void updateCallBackItem(int position);
	}
}
