package com.blue.blueapplication.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.adapter.SelectQuizAdapter;
import com.blue.blueapplication.domain.SelectItem;

import java.util.List;

public class QuizAlert {

	public static  interface SelectQuiz {

		public void confirmSelect(SelectItem applySelectDomain);

	}
	/**
	 * @param context
	 *            Context.
	 * @param title
	 *            The title of this AlertDialog can be null .
	 * @param items
	 *            button name list.
	 * @param alertDo
	 *            methods call Id:Button + cancel_Button.
	 * @param exit
	 *            Name can be null.It will be Red Color
	 * @return A AlertDialog
	 */
	public static Dialog showAlert(final Context context,
			final List<SelectItem> items,
			final SelectQuiz selectQuiz, final String currentSelect,int position) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_menu_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		final ListView list = (ListView) layout.findViewById(R.id.content_list);
		final TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
		tv_title.setText(currentSelect);
		if (items != null && items.size() >= 6) {
			ViewGroup.LayoutParams lp = list.getLayoutParams();
			lp.height = FrameApp.mApp.ui.DipToPixels(305);
		}
		SelectQuizAdapter adapter = new SelectQuizAdapter(context, items);
		list.setAdapter(adapter);
		list.setSelection(position);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SelectItem applySelectDomain = (SelectItem) parent
						.getItemAtPosition(position);

				if (applySelectDomain != null) {
					if (!applySelectDomain.des.equals(currentSelect)) {
						selectQuiz.confirmSelect(applySelectDomain);

					}
				}
				list.requestFocus();

				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// if (items!=null&&items.size()>6) {
		// lp.height = (int) (RongApplication.mApp.ui.DipToPixels(275));
		// }
		// lp.width = (int) (RongApplication.mApp.ui.getScreenWidth());
		dlg.onWindowAttributesChanged(lp);

		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		// }
		return dlg;
	}

}
