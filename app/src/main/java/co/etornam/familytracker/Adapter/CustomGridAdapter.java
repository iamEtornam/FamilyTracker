package co.etornam.familytracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomGridAdapter extends BaseAdapter {
	private List<Contact> listContact;
	private LayoutInflater layoutInflater;
	private Context mContext;

	public CustomGridAdapter(List<Contact> listContact, Context mContext) {
		this.listContact = listContact;
		this.mContext = mContext;
		layoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return listContact.size();
	}

	@Override
	public Object getItem(int position) {
		return listContact.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = layoutInflater.inflate(R.layout.single_contact_item, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}

		Contact contact = this.listContact.get(position);
		holder.txtContactName.setText(contact.getName());
		Picasso.get()
				.load(contact.getImageUrl())
				.error(R.drawable.ic_image_placeholder)
				.placeholder(R.drawable.ic_image_placeholder)
				.into(holder.imgContactProfile);
		holder.itemLayoutMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, " item: ", Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}


	static
	class ViewHolder {
		@BindView(R.id.imgContactProfile)
		CircleImageView imgContactProfile;
		@BindView(R.id.txtContactName)
		TextView txtContactName;
		@BindView(R.id.itemLayoutMain)
		LinearLayout itemLayoutMain;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
